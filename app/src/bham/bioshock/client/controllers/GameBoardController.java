package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.pathfinding.AStarPathfinding;

import java.util.ArrayList;
import java.util.Random;

import static bham.bioshock.common.consts.GridPoint.Type.*;


public class GameBoardController extends Controller {

    private GameBoard gameBoard;
    private Player mainPlayer;
    private GridPoint[][] grid;
    private AStarPathfinding pathFinder;


    public GameBoardController(Client client) {
        this.client = client;
        this.server = client.getServer();
        this.model = client.getModel();

        //TODO TEMP CODE REMOVE
        Player p1 = new Player(new Coordinates(0, 0), 0);
        Player p2 = new Player(new Coordinates(0, 35), 1);
        Player p3 = new Player(new Coordinates(35, 35), 2);
        Player p4 = new Player(new Coordinates(35, 0), 3);

        setMainPlayer(p1);

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
        grid = gameBoard.getGrid();
        pathFinder = new AStarPathfinding(grid, mainPlayer.getCoordinates(),36,36);
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

    public Player getMainPlayer() {
        return mainPlayer;
    }

    public void setMainPlayer(Player p) {
        this.mainPlayer = p;
    }

    public AStarPathfinding getPathFinder() {
        return pathFinder;
    }

    public boolean[] getPathColour(ArrayList<Coordinates> path) {
        boolean[] allowedMove = new boolean[path.size()];
        float fuel = mainPlayer.getFuel();
        for (int i = 0; i < path.size(); i++) {
            if (fuel < 10f) {
                allowedMove[i] = false;
            } else {
                allowedMove[i] = true;
                fuel -= 10;
            }
        }
        return allowedMove;
    }

    public void move(Coordinates destination){
        float fuel = mainPlayer.getFuel();

        ArrayList<Coordinates> path = pathFinder.pathfind(destination);

        // pathsize - 1 since path includes start position
        float pathCost = (path.size() - 1) * 10;

        // check if the player has enough fuel
        if(mainPlayer.getFuel() > pathCost) {
            mainPlayer.setCoordinates(destination);
            fuel -= pathCost;
            mainPlayer.setFuel(fuel);

            int x = destination.getX();
            int y = destination.getY();
            if(grid[x][y].getType() == PLANET)
                startMinigame();
            else if(grid[x][y].getType() == FUEL)
                mainPlayer.setFuel(fuel + 30);
            pathFinder.setStartPosition(mainPlayer.getCoordinates());
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