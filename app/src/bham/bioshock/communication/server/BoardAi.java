package bham.bioshock.communication.server;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.consts.GridPoint.Type;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.server.handlers.GameBoardHandler;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BoardAi extends Thread {

  private final Store store;
  private final GameBoardHandler gameBoardHandler;

  public BoardAi(Store store, GameBoardHandler gameBoardHandler) {
    this.store = store;
    this.gameBoardHandler = gameBoardHandler;
  }

  @Override
  public void run() {
    moveCpuPlayer();
  }

  /** Handle movement if the next player is a CPU */
  private void moveCpuPlayer() {
    // Get values from store
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();
    Player player = store.getMovingPlayer();

    // Gets list of possible moves and their rewards
    ArrayList<MoveVal> possibleMoves = generatePossibleMoves(store);

    // Picks the best move from the list
    //TODO Make this random?
    MoveVal bestMove = null;
    for (MoveVal mv: possibleMoves) {
      if (bestMove == null) {
        bestMove = mv;
      } else if (mv.getReward() > bestMove.getReward()) {
        bestMove = mv;
      }
    }
    // Generate a boardMove for the chosen move
    ArrayList<Coordinates> movePath = bestMove.getPath();
    player.createBoardMove(movePath);

    // Set player Cooordinates to final coordinate in the list
    player.setCoordinates(player.getBoardMove().get(player.getBoardMove().size()-1).getCoordinates());

    // Update fuel
    float pathCost = (movePath.size() - 1) * 10;
    player.decreaseFuel(pathCost);

    // Setup action arguments
    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add(gameBoard);
    arguments.add(player);

    Action action = new Action(Command.MOVE_PLAYER_ON_BOARD, arguments);
    gameBoardHandler.movePlayer(action);
  }

  /**
   * Generate all possible moves that a CPU could take
   */
  private ArrayList<MoveVal> generatePossibleMoves(Store store) {
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();
    Player player = store.getMovingPlayer();

    // Setup pathfinding
    AStarPathfinding pathFinder = new AStarPathfinding(grid, player.getCoordinates(),
        gameBoard.GRID_SIZE, gameBoard.GRID_SIZE, store.getPlayers());
    ArrayList<MoveVal> possibleMoves = new ArrayList();

    boolean isAdjecentPlanet;

    // Loop through all points available on the grid
    for (int x = 0; x < gameBoard.GRID_SIZE; x++) {
      for (int y = 0; y < gameBoard.GRID_SIZE; y++) {
        isAdjecentPlanet = false;
        GridPoint gridPoint = grid[x][y];
        GridPoint.Type type = gridPoint.getType();

        // Skip points that the CPU can't travel to i.e. Players and Asteroids
        if (type == GridPoint.Type.PLAYER || type == GridPoint.Type.ASTEROID
            || type == Type.PLANET) {
          continue;
          // Skips points the CPU doesn't have fuel to move to
        } else if ((Math.abs(x - player.getCoordinates().getX()) + Math
            .abs(y - player.getCoordinates().getY())) >= (player.getFuel() / 10)) {
          continue;
        }

        Player p;
        Coordinates moveCoords = new Coordinates(x, y);

        // Skips points that arn't next to planets or if the cpu player already owns the planet
        if (!gameBoard.isNextToThePlanet(moveCoords)) {
          continue;
        } else if ((p = gameBoard.getAdjacentPlanet(moveCoords).getPlayerCaptured()) != null) {
          if (p.equals(player)) {
            continue;
          } else {
            isAdjecentPlanet = true;
          }
        }

        // Attempt to generate path to the point
        ArrayList<Coordinates> path = pathFinder.pathfind(moveCoords);
        float pathCost = (path.size() - 1) * 10;

        // If it's possible to travel to that point, generate MoveVal object
        if (path.size() > 0 && player.getFuel() >= pathCost) {
          MoveVal moveVal = new MoveVal(moveCoords, path, pathCost);
          setReward(moveVal, player, isAdjecentPlanet, gameBoard, grid);
          possibleMoves.add(moveVal);
        }
      }
    }
    return possibleMoves;
  }

  /**
   * Calculates and sets the reward for a grid move for the ai
   * @param moveVal
   * @param player
   * @param isAdjecentPlanet
   * @param gameBoard
   */
  private void setReward(MoveVal moveVal, Player player, boolean isAdjecentPlanet,
      GameBoard gameBoard, GridPoint[][] grid) {
    // Initiate the reward to be the fuel cost of the move / 10
    float reward = -moveVal.getPathCost() / 10f;

    // Arraylist containing the planets that have been checked
    ArrayList<Planet> planetChecklist = new ArrayList<>();

    // Add 100 points if finishing next to a capturable planet
    if (isAdjecentPlanet) {
      reward += 100;
      moveVal.setCapturablePlanet(gameBoard.getAdjacentPlanet(moveVal.getMoveCoords()));
    }

    // Add two to the reward for each fuel box in the path
    for (Coordinates c: moveVal.getPath()) {
      if (gameBoard.getGridPoint(c).getType().equals(Type.FUEL)) {
        reward += 2;
      }
    }

    // Add 50 / distance to the planet for each capturable planet to the reward
    for (int x = 0; x < gameBoard.GRID_SIZE; x++) {
      for (int y = 0; y < gameBoard.GRID_SIZE; y++) {
        GridPoint gridPoint = grid[x][y];
        if (gridPoint.getType().equals(Type.PLANET) && !planetChecklist.contains(gridPoint.getValue())) {
          Planet planet = (Planet) gridPoint.getValue();
          if (planet.getPlayerCaptured() == null || !planet.getPlayerCaptured().equals(player)) {
            reward += 50f / moveVal.getMoveCoords().calcDistance(planet.getCoordinates());
            planetChecklist.add(planet);
          }
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
