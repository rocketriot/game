package bham.bioshock.server.handlers;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.math.Path;

public class GameBoardHandler {

  Store store;
  ServerHandler handler;

  public GameBoardHandler(Store store, ServerHandler handler) {
    this.store = store;
    this.handler = handler;
  }

  /** Adds a player to the server and sends the player to all the clients */
  public void getGameBoard(Action action) {
    GameBoard gameBoard = store.getGameBoard();

    // Generate a grid when starting the game
    if (gameBoard.getGrid() == null)
      store.generateGrid();

    ArrayList<Serializable> response = new ArrayList<>();
    response.add(gameBoard);
    for (Player p : store.getPlayers()) {
      response.add(p);
    }

    handler.sendToAll(new Action(Command.GET_GAME_BOARD, response));
  }

  /** Handles a player moving on their turn */
  public void movePlayer(Action action) {
    // Get game board and player from arguments
    ArrayList<Serializable> arguments = action.getArguments();
    GameBoard gameBoard = (GameBoard) arguments.get(0);
    Player movingPlayer = (Player) arguments.get(1);

    // Update the store
    store.setGameBoard(gameBoard);
    store.updatePlayer(movingPlayer);

    // Send out new game board and moving player to players
    ArrayList<Serializable> response = new ArrayList<>();
    response.add(gameBoard);
    response.add(movingPlayer);
    handler.sendToAll(new Action(Command.MOVE_PLAYER_ON_BOARD, response));

    store.nextTurn();

    // Handle if the next player is a CPU
    if (store.getMovingPlayer().isCpu())
      moveCpuPlayer();
  }

  /** Handle movement if the next player is a CPU */
  private void moveCpuPlayer() {
    // Get values from store
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();
    Player player = store.getMovingPlayer();
    
    HashMap<GridPoint.Type, ArrayList<ArrayList<Coordinates>>> possibleMoves = generatePossibleMoves(store);

    float randomFloat = (new Random()).nextFloat();

    if (randomFloat <= 0.015) {

    }

    // Find all possible spaces the CPU can go
    // Probability of choosing a specific space: EMPTY <= FUEL <= PLANET

    // Setup action arguments
    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add(gameBoard);
    arguments.add(player);

    Action action = new Action(Command.MOVE_PLAYER_ON_BOARD, arguments);
    movePlayer(action);
  }

  /** Generate all possible moves that a CPU could take */
  private HashMap<GridPoint.Type, ArrayList<ArrayList<Coordinates>>> generatePossibleMoves(Store store) {
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
        if (
          type != GridPoint.Type.PLAYER ||
          type != GridPoint.Type.ASTEROID
        ) continue;

        // Attempt to generate path to the point
        ArrayList<Coordinates> path = pathFinder.pathfind(new Coordinates(x, y));

        // If it's possible to travel to that point, add path to possible moves
        if (!path.isEmpty())
          possibleMoves.get(type).add(path);
    }

    return possibleMoves;
  }
}
