package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.common.models.*;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.pathfinding.AStarPathfinding;

import java.util.ArrayList;
import java.util.Random;

import static bham.bioshock.common.consts.GridPoint.Type.*;

public class GameBoardController implements Controller {
    private Client client;
    private Model model;
    private GameBoard gameBoard;
    private GridPoint[][] grid;

    public GameBoardController(Client client, Model model) {
        this.client = client;
        this.model = model;

        gameBoard = model.getGameBoard();

        try {
            ArrayList<Player> players = model.getPlayers();
            gameBoard.generateGrid(players);
        } catch (Exception e) {
            // Handle no players error
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