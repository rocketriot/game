package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.common.models.Model;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.client.ClientService;

import java.util.ArrayList;

public class GameBoardController implements Controller {
    private Client client;
    private ClientService server;
    private Model model;
    private GameBoard gameBoard;

    public GameBoardController(Client client) {
        this.client = client;
        this.server = client.getServer();
        this.model = client.getModel();

        gameBoard = model.getGameBoard();

        try {
            ArrayList<Player> players = model.getPlayers();
            gameBoard.generateGrid(players);
        } catch (Exception e) {
            // Handle no players error
        }
    }

    public GridPoint[][] getGrid() {
        return gameBoard.getGrid();
    }

    public ArrayList<Player> getPlayers() {
        return model.getPlayers();
    }

    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }
}