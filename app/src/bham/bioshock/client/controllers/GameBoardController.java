package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Model;
import bham.bioshock.common.models.Player;

import java.util.ArrayList;

public class GameBoardController implements Controller {
    private Client client;
    private Model model;
    private GameBoard gameBoard;

    public GameBoardController(Client client, Model model) {
        this.client = client;
        this.model = model;

        //TODO TEMP CODE REMOVE
        Player p1 = new Player(new Coordinates(0, 0), 0);
        Player p2 = new Player(new Coordinates(0, 35), 1);
        Player p3 = new Player(new Coordinates(35, 35), 2);
        Player p4 = new Player(new Coordinates(35, 0), 3);
        Player[] players = {p1, p2, p3, p4};
        model.createGameBoard(players);

        gameBoard = model.getGameBoard();

        try {
            ArrayList<Player> players = model.getPlayers();
            gameBoard.generateGrid(players);
        } catch (Exception e) {
            // Handle no players error
            System.err.println("No Players: ");
            e.printStackTrace();
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