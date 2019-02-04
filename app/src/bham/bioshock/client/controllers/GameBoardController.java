package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.common.models.*;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.client.screens.GameBoardScreen;
import bham.bioshock.communication.client.ClientService;
import static bham.bioshock.common.consts.GridPoint.Type.*;

import java.util.ArrayList;
import java.util.Random;


public class GameBoardController extends Controller {

    private GameBoard gameBoard;
    private GridPoint[][] grid;

    public GameBoardController(Client client) {
        this.client = client;
        this.server = client.getServer();
        this.model = client.getModel();

        // TODO TEMP CODE REMOVE
        Player p1 = new Player();
        Player p2 = new Player();
        Player p3 = new Player();
        Player p4 = new Player();

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
        GridPoint[][] grid = gameBoard.getGrid();
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

}