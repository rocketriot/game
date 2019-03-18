package bham.bioshock.communication.server;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.consts.GridPoint.Type;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
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

    // Setup random
    Random random = new Random();

    // Attempts to path to planets
    HashMap<Type, ArrayList<ArrayList<Coordinates>>> possibleMoves = generatePossibleMoves(store, true);

    // Path to random location if a move to a planet isn't possible
    if (possibleMoves.size() == 0)
      possibleMoves = generatePossibleMoves(store, false);

    // Picks and random move from list of possible moves
    ArrayList<ArrayList<Coordinates>> pathList = possibleMoves.get(Type.EMPTY);
    ArrayList<Coordinates> movePath = pathList.get(random.nextInt(pathList.size()));
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
    gameBoardHandler.movePlayer(action, player.getId());
  }

  /** Generate all possible moves that a CPU could take */
  private HashMap<GridPoint.Type, ArrayList<ArrayList<Coordinates>>> generatePossibleMoves(
      Store store, boolean checkPlanetMoves) {
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();
    Player player = store.getMovingPlayer();

    // Setup pathfinding
    AStarPathfinding pathFinder = new AStarPathfinding(grid, player.getCoordinates(), gameBoard.GRID_SIZE, gameBoard.GRID_SIZE, store.getPlayers());
    HashMap<GridPoint.Type, ArrayList<ArrayList<Coordinates>>> possibleMoves = new HashMap<>();

    // Loop through all points available on the grid
    for (int x = 0; x < gameBoard.GRID_SIZE; x++) {
      for (int y = 0; y < gameBoard.GRID_SIZE; y++) {
        GridPoint gridPoint = grid[x][y];
        GridPoint.Type type = gridPoint.getType();

        // Skip points that the CPU can't travel to i.e. Players and Asteroids
        if (type == GridPoint.Type.PLAYER || type == GridPoint.Type.ASTEROID || type == Type.PLANET) {
          continue;
          // Skips points the CPU doesn't have fuel to move to
        } else if ((Math.abs(x - player.getCoordinates().getX()) + Math.abs(y - player.getCoordinates().getY())) >= (player.getFuel() / 10)) {
          continue;
        }
        Coordinates moveCoords = new Coordinates(x, y);
        Player p;

        // Skips points that arn't next to planets or if the cpu player already owns the planet
        if (checkPlanetMoves) {
          if (!gameBoard.isNextToThePlanet(moveCoords))
            continue;
          else if ((p = gameBoard.getAdjacentPlanet(moveCoords).getPlayerCaptured()) != null)
            if (p.equals(player))
              continue;
        }

        // Attempt to generate path to the point
        ArrayList<Coordinates> path = pathFinder.pathfind(moveCoords);
        float pathCost = (path.size() - 1) * 10;

        // If it's possible to travel to that point, add path to possible moves
        if (path.size() > 0 && player.getFuel() >= pathCost) {
          if (possibleMoves.get(type) == null) {
            ArrayList<ArrayList<Coordinates>> initialArray = new ArrayList<>();
            initialArray.add(path);
            possibleMoves.put(type, initialArray);
          } else {
            possibleMoves.get(type).add(path);
          }
        }
      }
    }

    return possibleMoves;
  }
}
