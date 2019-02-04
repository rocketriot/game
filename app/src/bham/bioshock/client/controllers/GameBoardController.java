package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.screens.GameBoardScreen;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Model;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.client.ClientService;

import java.util.ArrayList;

import com.badlogic.gdx.Screen;

public class GameBoardController implements Controller {
    private Client client;
    private ClientService server;
    private GameBoardScreen screen;
    private Model model;
    private GameBoard gameBoard;

    public GameBoardController(Client client) {
        this.client = client;
        this.server = client.getServer();
        this.model = client.getModel();

        // TODO TEMP CODE REMOVE
        Player p1 = new Player(new Coordinates(0, 0), 0);
        Player p2 = new Player(new Coordinates(0, 35), 1);
        Player p3 = new Player(new Coordinates(35, 35), 2);
        Player p4 = new Player(new Coordinates(35, 0), 3);

        model.createGameBoard();
        model.addPlayer(p1);
        model.addPlayer(p2);
        model.addPlayer(p3);
        model.addPlayer(p4);

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

    public void setScreen(Screen screen) {
        this.screen = (GameBoardScreen) screen;
    }

    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }
}