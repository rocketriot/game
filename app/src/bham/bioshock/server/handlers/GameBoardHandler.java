package bham.bioshock.server.handlers;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.consts.GridPoint.Type;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.Player.Move;
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
    Player currentPlayer = store.getPlayer(movingPlayer.getId());
    currentPlayer.setCoordinates(movingPlayer.getCoordinates());
    currentPlayer.setFuel(movingPlayer.getFuel());

    // Send out new game board and moving player to players
    ArrayList<Serializable> response = new ArrayList<>();
    response.add(gameBoard);
    response.add(movingPlayer);
    
    handler.sendToAll(new Action(Command.MOVE_PLAYER_ON_BOARD, response));

    int waitTime = calculateMoveTime(currentPlayer.getBoardMove());
    new Thread(() -> {
      try {
        Thread.sleep(waitTime);
        endTurn();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();
  }

  private int calculateMoveTime(ArrayList<Move> boardMove) {
    // Players move 3 tiles per second + 500 to prevent race condition
    if (boardMove != null)
      return (boardMove.size() * 1000)/3 + 500;
    else
      return 0;
  }

  public void endTurn() {
    store.nextTurn();
    handler.sendToAll(new Action(Command.UPDATE_TURN));
    // Handle if the next player is a CPU
    Player movingPlayer = store.getMovingPlayer();
    if (movingPlayer.isCpu()) {
      new BoardAi(store, this).run();
    }
  }
}
