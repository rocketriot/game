package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.common.models.Model;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;

public class GameBoardController implements Controller {
    private Client client;
    private Model model;
    private GameBoard gameBoard;

    public GameBoardController(Client client, Model model) {
        this.client = client;
        this.model = model;

        gameBoard = model.getGameBoard();

        try {
            gameBoard.generateGrid();
        } catch (Exception e) {
            // Handle no players error
        }
    }

    public GridPoint[][] getGrid() {
        return gameBoard.getGrid();
    }

    public Player[] getPlayers() {
        return gameBoard.getPlayers();
    }
}