package bham.bioshock.server.handlers;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Model;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;

import java.io.Serializable;
import java.util.ArrayList;

public class GameBoardHandler {
  /** Creates a game board (if needed) and sends it to the clients */
  public static void getGameBoard(Model model, Action action, ServerHandler handler)
      throws Exception {
    GameBoard gameBoard = model.getGameBoard();

    // Generate a grid when starting the game
    if (gameBoard.getGrid() == null) model.generateGrid();

    ArrayList<Serializable> response = new ArrayList<>();
    response.add(gameBoard);

    handler.sendToAll(new Action(Command.GET_GAME_BOARD, response));
  }

  /** Handles a player moving on their turn */
  public static void movePlayer(Model model, Action action, ServerHandler handler) {
    // Get game board and player from arguments and update the model
    ArrayList<Serializable> arguments = action.getArguments();
    GameBoard gameBoard = (GameBoard) arguments.get(0);
    Player movingPlayer = (Player) arguments.get(1);
    model.setGameBoard(gameBoard);
    model.updatePlayer(movingPlayer);

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
  }
}
