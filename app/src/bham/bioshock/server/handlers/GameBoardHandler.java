package bham.bioshock.server.handlers;

import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;

import java.io.Serializable;
import java.util.ArrayList;

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
  }
}
