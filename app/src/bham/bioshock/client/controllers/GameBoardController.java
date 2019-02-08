package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.Client.View;
import bham.bioshock.client.screens.GameBoardScreen;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;

import com.badlogic.gdx.Screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static bham.bioshock.common.consts.GridPoint.Type.*;

public class GameBoardController extends Controller {
    private Client client;
    private ClientService server;

    private Model model;
    private GameBoard gameBoard;

    public GameBoardController(Client client) {
        this.client = client;
        this.server = client.getServer();
        this.model = client.getModel();
        gameBoard = model.getGameBoard();
    }

    /** When the game board is on the screen */
    public void onShow() {
        // Update server connection from null since we should be connected
        server = client.getServer();

        // If the grid is not yet loaded, go to loading screen and fetch the game board
        // from the server
        if (gameBoard.getGrid() == null) {
            server.send(new Action(Command.GET_GAME_BOARD));
            client.changeScreen(View.LOADING);
        }
    }

    /** Handles when the server sends the game board to the client */
    public void gameBoardReceived(Action action) {
        // Update gameboard from arguments
        ArrayList<Serializable> arguments = action.getArguments();
        gameBoard = (GameBoard) arguments.get(0);

        client.changeScreen(View.GAME_BOARD);
    }

    public GridPoint[][] getGrid() {
        return gameBoard.getGrid();
    }

    public int getGridSize() {
        return gameBoard.GRID_SIZE;
    }

    public ArrayList<Player> getPlayers() {
        return model.getPlayers();
    }

    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }

    public void move(Player player, Coordinates destination) {
        GridPoint[][] grid = gameBoard.getGrid();

        Coordinates currentPosition = player.getCoordinates();
        float fuel = player.getFuel();

        AStarPathfinding pathFinder = new AStarPathfinding(grid, currentPosition, gameBoard.GRID_SIZE,
                gameBoard.GRID_SIZE);
        ArrayList<Coordinates> path = pathFinder.pathfind(destination);

        // check if the player has enough fuel
        if (player.getFuel() > path.size()) {
            player.setCoordinates(destination);
            fuel = fuel - path.size();
            player.setFuel(fuel - path.size());

            int x = destination.getX();
            int y = destination.getY();
            if (grid[x][y].getType() == PLANET)
                startMinigame();
            else if (grid[x][y].getType() == FUEL)
                player.setFuel(fuel + 3);
        }
    }

    public void startMinigame() {

    }

    public void miniGameWon(Player player, Planet planet) {
        // winner gets the planet, previous owner loses it
        if (planet.getPlayerCaptured() != null) {
            Player loser = planet.getPlayerCaptured();
            loser.setPlanetsCaptured(loser.getPlanetsCaptured() - 1);
        }
        planet.setPlayerCaptured(player);
        player.setPlanetsCaptured(player.getPlanetsCaptured() + 1);
        player.setPoints(player.getPoints() + 100);
    }

    public void miniGameLost(Player player) {
        GridPoint[][] grid = gameBoard.getGrid();

        // if player attacks planet and doesn't win gets moved in a random position
        int x, y;
        do {
            x = new Random().nextInt();
            y = new Random().nextInt();
        } while (grid[x][y].getType() != EMPTY);

        Coordinates newCoordinates = new Coordinates(x, y);
        player.setCoordinates(newCoordinates);
    }

    public void setScreen(Screen screen) {
        this.screen = (GameBoardScreen) screen;
    }
}