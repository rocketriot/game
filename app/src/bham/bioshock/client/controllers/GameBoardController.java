package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.screens.GameBoardScreen;
import bham.bioshock.common.models.*;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;

import static bham.bioshock.common.consts.GridPoint.Type.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Screen;

public class GameBoardController extends Controller {
    private Client client;
    private ClientService server;
    private GameBoardScreen screen;

    private Model model;
    private GameBoard gameBoard;
    private GridPoint[][] grid;

    public GameBoardController(Client client) {
        this.client = client;
        this.server = client.getServer();
        this.model = client.getModel();

        gameBoard = model.getGameBoard();
    }

    /** When the game board is on the screen */
    public void onShow() {
        // Fetch the game board from the server
        server.send(new Action(Command.GET_GAME_BOARD));
    }

    /** Handles when the server sends the game board to the client */
    public void gameBoardReceived(Action action) {
        ArrayList<Serializable> arguments = action.getArguments();
        grid = (GridPoint[][]) arguments.get(0);

        gameBoard.setGrid(grid);
        screen.updateGrid(grid);
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

    public void move(Player player, Coordinates destination){
        Coordinates currentPosition = player.getCoordinates();
        float fuel = player.getFuel();

        AStarPathfinding pathFinder = new AStarPathfinding(grid, currentPosition,36,36);
        ArrayList<Coordinates> path = pathFinder.pathfind(destination);

        // check if the player has enough fuel
        if(player.getFuel() > path.size()) {
            player.setCoordinates(destination);
            fuel = fuel- path.size();
            player.setFuel(fuel- path.size());

            int x = destination.getX();
            int y= destination.getY();
            if(grid[x][y].getType() == PLANET)
                startMinigame();
            else if(grid[x][y].getType() == FUEL)
                player.setFuel(fuel + 3);
        }
    }

    public void startMinigame(){

    }

    public void miniGameWon(Player player, Planet planet) {
        // winner gets the planet, previous owner loses it
        if(planet.getPlayerCaptured() != null) {
            Player loser = planet.getPlayerCaptured();
            loser.setPlanetsCaptured(loser.getPlanetsCaptured() - 1);
        }
        planet.setPlayerCaptured(player);
        player.setPlanetsCaptured(player.getPlanetsCaptured() + 1);
        player.setPoints(player.getPoints() + 100);
    }

    public void miniGameLost(Player player){
       // if player attacks planet and doesn't win gets moved in a random position
        int x,y;
        do {
            x = new Random().nextInt();
            y = new Random().nextInt();
        } while(grid[x][y].getType() != EMPTY);

        Coordinates newCoordinates = new Coordinates(x,y);
        player.setCoordinates(newCoordinates);
    }

    public void setScreen(Screen screen) {
        this.screen = (GameBoardScreen) screen;
    }

}