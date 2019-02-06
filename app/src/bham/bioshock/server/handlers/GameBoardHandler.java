package bham.bioshock.server.handlers;

import java.io.Serializable;
import java.util.ArrayList;

import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Model;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;

public class GameBoardHandler {
    /**
     * Adds a player to the server and sends the player to all the clients
     */
    public static void getGameBoard(Model model, Action action, ServerHandler handler) {
        GameBoard gameBoard = model.getGameBoard();

        // Create an initial game board when starting the game
        if (gameBoard == null) {
            gameBoard = model.createGameBoard();
        }

        ArrayList<Serializable> response = new ArrayList<>();
        response.add(gameBoard);

        handler.sendToAll(new Action(Command.GET_GAME_BOARD, response));
    }
}
