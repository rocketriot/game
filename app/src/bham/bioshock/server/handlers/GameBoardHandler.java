package bham.bioshock.server.handlers;

import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;

import java.io.Serializable;
import java.util.ArrayList;

public class GameBoardHandler {
  /** Adds a player to the server and sends the player to all the clients */
  public static void getGameBoard(Store model, Action action, ServerHandler handler)
      throws Exception {
    GameBoard gameBoard = model.getGameBoard();

    // Generate a grid when starting the game
    if (gameBoard.getGrid() == null) model.generateGrid();

    ArrayList<Serializable> response = new ArrayList<>();
    response.add(gameBoard);

    handler.sendToAll(new Action(Command.GET_GAME_BOARD, response));
  }
}
