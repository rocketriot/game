package bham.bioshock.server.handlers;

import bham.bioshock.client.Route;
import bham.bioshock.common.models.Planet;
import java.io.Serializable;
import java.util.ArrayList;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.Player.Move;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.BoardAi;
import bham.bioshock.server.ServerHandler;

import java.util.UUID;

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

    if (movingPlayer.isCpu()) {
      int waitTime = calculateMoveTime(currentPlayer.getBoardMove());
      new Thread(() -> {
        try {
          Thread.sleep(waitTime);
          startMinigame(gameBoard, currentPlayer);
          endTurn(movingPlayer.getId());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }).start();
    }
  }

  private void startMinigame(GameBoard gameBoard, Player currentPlayer) {
    Planet planet;
    if ((planet = gameBoard.getAdjacentPlanet(currentPlayer.getCoordinates(), currentPlayer)) != null) {
      //TODO Handle minigame start
      boolean minigameRunning = true;
      while (minigameRunning) {
        try {
          Thread.sleep(500);
          //TODO Handle minigame end
          minigameRunning = false;
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private int calculateMoveTime(ArrayList<Move> boardMove) {
    // Players move 3 tiles per second + 500 to prevent race condition
    if (boardMove != null)
      return (boardMove.size() * 1000)/3 + 500;
    else
      return 0;
  }

  public void endTurn(UUID id) {
    handler.sendToAll(new Action(Command.UPDATE_TURN));
    // Handle if the next player is a CPU
    new Thread(() -> {
      try {
        int waitTime = 100;
        while(store.getMovingPlayer().getId().equals(id)) {
          Thread.sleep(waitTime);
        }

        if (store.getMovingPlayer().isCpu())
          new BoardAi(store, this).run();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();
  }
}
