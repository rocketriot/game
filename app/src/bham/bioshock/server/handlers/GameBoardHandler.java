package bham.bioshock.server.handlers;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.consts.GridPoint.Type;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.BoardAi;
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
  
  private void generateGrid(GameBoard board, ArrayList<Player> players) {
    // Set coordinates of the players
    int last = board.GRID_SIZE - 1;
    players.get(0).setCoordinates(new Coordinates(0, 0));
    players.get(1).setCoordinates(new Coordinates(0, last));
    players.get(2).setCoordinates(new Coordinates(last, last));
    players.get(3).setCoordinates(new Coordinates(last, 0));

    board.generateGrid();
  }

  /** Adds a player to the server and sends the player to all the clients */
  public void getGameBoard(Action action, ArrayList<Player> additionalPlayers) {
    ArrayList<Player> players = store.getPlayers();
    if(additionalPlayers != null) {
      players.addAll(additionalPlayers);
    }
    
    GameBoard gameBoard = store.getGameBoard();
    // Generate a grid when starting the game

    if (gameBoard == null) {
      gameBoard = new GameBoard(); 
      generateGrid(gameBoard, players);
    }

    ArrayList<Serializable> response = new ArrayList<>();
    response.add(gameBoard);
    for (Player p : players) {
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
    Player p = store.getPlayer(movingPlayer.getId());
    p.setCoordinates(movingPlayer.getCoordinates());
    p.setFuel(movingPlayer.getFuel());

    // Send out new game board and moving player to players
    ArrayList<Serializable> response = new ArrayList<>();
    response.add(gameBoard);
    response.add(movingPlayer);
    
    handler.sendToAll(new Action(Command.MOVE_PLAYER_ON_BOARD, response));

    store.nextTurn();

    // Handle if the next player is a CPU
    if (store.getMovingPlayer().isCpu())
      new BoardAi(store, this).run();
      //moveCpuPlayer();
  }

  /** Handle movement if the next player is a CPU */
  private void moveCpuPlayer() {
    // Get values from store
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();
    Player player = store.getMovingPlayer();
    
    HashMap<GridPoint.Type, ArrayList<ArrayList<Coordinates>>> possibleMoves = generatePossibleMoves(store);

    Random random = new Random();
    ArrayList<ArrayList<Coordinates>> pathList = possibleMoves.get(Type.EMPTY);
    ArrayList<Coordinates> movePath = pathList.get(random.nextInt(pathList.size()));
    player.createBoardMove(movePath);

    // Set player Cooordinates to final coordinate in the list
    player.setCoordinates(player.getBoardMove().get(player.getBoardMove().size()-1).getCoordinates());

    float pathCost = (movePath.size() - 1) * 10;
    player.decreaseFuel(pathCost);

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
        if (type == GridPoint.Type.PLAYER || type == GridPoint.Type.ASTEROID || type == Type.PLANET) {
          continue;
        }

        // Attempt to generate path to the point
        ArrayList<Coordinates> path = pathFinder.pathfind(new Coordinates(x, y));
        float pathCost = (path.size() - 1) * 10;

        // If it's possible to travel to that point, add path to possible moves
        if (path.size() > 0 && path.size() <= pathCost) {
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
