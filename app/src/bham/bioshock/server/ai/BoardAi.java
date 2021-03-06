package bham.bioshock.server.ai;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.consts.GridPoint.Type;
import bham.bioshock.common.models.*;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.communication.messages.boardgame.MovePlayerOnBoardMessage;
import bham.bioshock.server.handlers.GameBoardHandler;
import bham.bioshock.server.handlers.MinigameHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.UUID;

public class BoardAi extends Thread {

  private static final Logger logger = LogManager.getLogger(BoardAi.class);

  private final Store store;
  private final GameBoardHandler gameBoardHandler;
  private final MinigameHandler minigameHandler;
  private UUID lastMoved = null;

  public BoardAi(Store store, GameBoardHandler gameBoardHandler, MinigameHandler minigameHandler) {
    super("BoardAi");
    this.store = store;
    this.gameBoardHandler = gameBoardHandler;
    this.minigameHandler = minigameHandler;
  }

  @Override
  public void run() {
    int samePlayerNum = 0;
    UUID lastPlayer = null;
    int turnNum = 0;
    int currentNum = -1;

    try {
      while (!isInterrupted()) {
        if (store.getPlayers().size() == 0) {
          sleep(500);
          continue;
        }

        turnNum = store.getRound() * 4 + store.getTurn();

        Player player = store.getMovingPlayer();
        if (player.isCpu()
            && lastPlayer != null
            && lastPlayer.equals(player.getId())
            && store.getMinigameStore() == null) {
          samePlayerNum++;
        } else {
          samePlayerNum = 0;
        }

        if (samePlayerNum > 10) {
          logger.error("Forced player skip");
          gameBoardHandler.endTurn();
          samePlayerNum = 0;
          continue;
        }

        // Last moved
        if (player.isCpu() && currentNum < turnNum) {
          // Make a move
          ArrayList<Coordinates> path = moveCpuPlayer();
          int waitTime = calculateMoveTime(player, path);
          Thread.sleep(waitTime);

          GameBoard gameBoard = store.getGameBoard();
          Planet planet = gameBoard.getAdjacentPlanet(player.getCoordinates(), player);

          currentNum = turnNum;
          // Make a decision after a move
          if (planet != null && player.getFuel() >= (3 * player.getFuelGridCost())) {
            minigameHandler.startMinigame(player.getId(), planet.getId(), gameBoardHandler, null);
          } else {
            gameBoardHandler.endTurn();
          }
        }
        lastPlayer = player.getId();
        sleep(1000);
      }
    } catch (InterruptedException e) {
      logger.info("Board AI interrupted");
    }
    logger.fatal("AI finished");
  }

  private int calculateMoveTime(Player player, ArrayList<Coordinates> path) {
    // Players move 3 tiles per second + 500 to prevent race condition
    if (path != null) return (path.size() * 1000) / 3 + 500;
    else return 0;
  }

  /**
   * Handle movement if the next player is a CPU
   *
   * @return
   */
  private ArrayList<Coordinates> moveCpuPlayer() {
    // Get values from store
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();
    Player player = store.getMovingPlayer();

    // Gets list of possible moves and their rewards
    ArrayList<MoveVal> possibleMoves = generatePossibleMoves(store);

    // Picks the best move from the list
    MoveVal bestMove = null;
    for (MoveVal mv : possibleMoves) {
      if (bestMove == null) {
        bestMove = mv;
      } else if (mv.getReward() > bestMove.getReward()) {
        bestMove = mv;
      }
    }

    if (bestMove != null) {
      MovePlayerOnBoardMessage msg =
          new MovePlayerOnBoardMessage(bestMove.getMoveCoords(), player.getId(), null);
      gameBoardHandler.movePlayer(msg, player.getId());
      return bestMove.getPath();
    }
    return new ArrayList<Coordinates>();
  }

  /** Generate all possible moves that a CPU could take */
  private ArrayList<MoveVal> generatePossibleMoves(Store store) {
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();
    Player player = store.getMovingPlayer();

    // Setup pathfinding
    AStarPathfinding pathFinder =
        new AStarPathfinding(
            grid,
            player.getCoordinates(),
            gameBoard.GRID_SIZE,
            gameBoard.GRID_SIZE,
            store.getPlayers());
    ArrayList<MoveVal> possibleMoves = new ArrayList();

    // Loop through all points available on the grid
    for (int x = 0; x < gameBoard.GRID_SIZE; x++) {
      for (int y = 0; y < gameBoard.GRID_SIZE; y++) {
        GridPoint gridPoint = grid[x][y];
        GridPoint.Type type = gridPoint.getType();

        // Skip points that the CPU can't travel to i.e. Players and Asteroids
        if (type == GridPoint.Type.PLAYER
            || type == GridPoint.Type.ASTEROID
            || type == Type.PLANET) {
          continue;
          // Skips points the CPU doesn't have fuel to move to
        } else if ((Math.abs(x - player.getCoordinates().getX())
                + Math.abs(y - player.getCoordinates().getY()))
            >= (player.getFuel() / 10)) {
          continue;
        }

        Player p;
        Coordinates moveCoords = new Coordinates(x, y);

        // Attempt to generate path to the point
        ArrayList<Coordinates> path = pathFinder.pathfind(moveCoords);
        float pathCost = (path.size() - 1) * player.getFuelGridCost();

        // Skip points if no path is available
        if (path.size() == 0) {
          continue;
        }

        // If it's possible to travel to that point, generate MoveVal object
        if (path.size() > 0 && player.getFuel() >= pathCost) {
          MoveVal moveVal = new MoveVal(moveCoords, path, pathCost);
          setReward(moveVal, player, gameBoard, grid);
          possibleMoves.add(moveVal);
        }
      }
    }
    return possibleMoves;
  }

  /**
   * Calculates and sets the reward for a grid move for the ai
   *
   * @param moveVal
   * @param player
   * @param gameBoard
   */
  private void setReward(MoveVal moveVal, Player player, GameBoard gameBoard, GridPoint[][] grid) {
    // Initiate the reward to be the fuel cost of the move / 10
    float reward = -moveVal.getPathCost() / 10f;

    // Add two to the reward for each fuel box in the path
    // Add 75 points per upgrade in the path
    // Set the reward to 50 + fuel cost if pathing through a black hole
    for (Coordinates c : moveVal.getPath()) {
      if (gameBoard.getGridPoint(c).getType().equals(Type.FUEL)) {
        reward += (2 * player.getFuelGridCost()) / 10f;
      } else if (gameBoard.getGridPoint(c).getType().equals(Type.UPGRADE)) {
        reward += 75;
      } else if (gameBoard.getGridPoint(c).getType().equals(Type.BLACKHOLE)) {
        moveVal.setReward(reward + 50);
        return;
      }
    }

    // Arraylist containing the planets that have been checked
    ArrayList<Planet> planetChecklist = new ArrayList<>();

    // Add 100 points if finishing next to a capturable planet
    // Add a further 150 if the player has enough fuel remaining to capture it
    Planet planet;
    if ((planet = gameBoard.getAdjacentPlanet(moveVal.getMoveCoords(), player)) != null) {
      reward += 100;
      moveVal.setCapturablePlanet(planet);
      if ((player.getFuel() - moveVal.getPathCost()) > 3 * player.getFuelGridCost()) reward += 150;
    }

    // Add 50 / distance to the planet for each capturable planet and upgrade
    for (int x = 0; x < gameBoard.GRID_SIZE; x++) {
      for (int y = 0; y < gameBoard.GRID_SIZE; y++) {
        GridPoint gridPoint = grid[x][y];
        if (gridPoint.getType().equals(Type.PLANET)
            && !planetChecklist.contains(gridPoint.getValue())) {
          planet = (Planet) gridPoint.getValue();
          if (planet.getPlayerCaptured() == null || !planet.getPlayerCaptured().equals(player)) {
            float value = 30 - moveVal.getMoveCoords().calcDistance(planet.getCoordinates());
            if (value > 0) reward += value;
            planetChecklist.add(planet);
          }
        } else if (gridPoint.getType().equals(Type.UPGRADE)) {
          Upgrade upgrade = (Upgrade) gridPoint.getValue();
          float value = 30 - moveVal.getMoveCoords().calcDistance(upgrade.getCoordinates());
          if (value > 0) reward += value;
        }
      }
    }

    moveVal.setReward(reward);
  }

  private class MoveVal {
    private float reward;
    private Planet capturablePlanet;
    private Coordinates moveCoords;
    private ArrayList<Coordinates> path;
    private float pathCost;

    public MoveVal(Coordinates moveCoords, ArrayList<Coordinates> path, float pathCost) {
      this.moveCoords = moveCoords;
      this.path = path;
      this.pathCost = pathCost;
    }

    public float getReward() {
      return reward;
    }

    public void setReward(float reward) {
      this.reward = reward;
    }

    public Planet getCapturablePlanet() {
      return capturablePlanet;
    }

    public void setCapturablePlanet(Planet capturablePlanet) {
      this.capturablePlanet = capturablePlanet;
    }

    public ArrayList<Coordinates> getPath() {
      return path;
    }

    public Coordinates getMoveCoords() {
      return moveCoords;
    }

    public float getPathCost() {
      return pathCost;
    }
  }
}
