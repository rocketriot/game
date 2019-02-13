package bham.bioshock.server.handlers;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;

import java.io.Serializable;
import java.util.ArrayList;

public class GameBoardHandler {
  /** Adds a player to the server and sends the player to all the clients */
  public static void getGameBoard(Store store, Action action, ServerHandler handler) {
    GameBoard gameBoard = store.getGameBoard();

    // Generate a grid when starting the game
    if (gameBoard.getGrid() == null) store.generateGrid();

    ArrayList<Serializable> response = new ArrayList<>();
    response.add(gameBoard);
    response.add(store.getPlayers());
    handler.sendToAll(new Action(Command.GET_GAME_BOARD, response));
  }

  /** Handles a player moving on their turn */
  public static void movePlayer(Store store, Action action, ServerHandler handler) {
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

    // Get grid point the user landed on
    GridPoint gridPoint = gameBoard.getGridPoint(movingPlayer.getCoordinates());

    // If the player landed on a planet, start a minigame
    if (gridPoint.getType() == GridPoint.Type.PLANET) {
      // TODO: randomly pick a minigame type
      handler.sendToAll(new Action(Command.START_MINIGAME));
    }

    store.nextTurn();
  }
}
