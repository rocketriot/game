package bham.bioshock.server.handlers;

import java.util.ArrayList;

import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Model;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerService;

public class GameBoardHandler {
    /**
     * Adds a player to the server and sends the player to all the clients
     */
    public static void getGameBoard(Model model, Action action, ServerService service) {
        GameBoard gameBoard = model.getGameBoard();

        // Create an initial game board when starting the game
        if (gameBoard == null) {
            gameBoard = model.createGameBoard();

        }

        // TODO: serialize the grid
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(gameBoard.getGrid().toString());

//        service.send(new Action(Command.GET_GAME_BOARD, arguments));
    }
}
