package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.Client.View;
import bham.bioshock.client.screens.GameBoardScreen;
import bham.bioshock.common.Direction;
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
    private Player mainPlayer;
    private AStarPathfinding pathFinder;
    private boolean receivedGrid = false;

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
        if (receivedGrid == false) {
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

        receivedGrid = true;

        //TODO change to client's player
        setMainPlayer(model.getPlayers().get(0));
        pathFinder = new AStarPathfinding(gameBoard.getGrid(), mainPlayer.getCoordinates(),36,36);
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

    public Player getMainPlayer() {
        return mainPlayer;
    }

    public void setMainPlayer(Player p) {
        this.mainPlayer = p;
    }

    public AStarPathfinding getPathFinder() {
        pathFinder.setStartPosition(mainPlayer.getCoordinates());
        return pathFinder;
    }

    public boolean[] getPathColour(ArrayList<Coordinates> path) {
        if (path != null) {
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
        } else {
            return null;
        }
    }

    public void move(Coordinates destination){
        float fuel = mainPlayer.getFuel();
        GridPoint[][] grid = gameBoard.getGrid();
        ArrayList<Coordinates> path = pathFinder.pathfind(destination);
        Coordinates playerCoords = mainPlayer.getCoordinates();

        // pathsize - 1 since path includes start position
        float pathCost = (path.size() - 1) * 10;

        // check if the player has enough fuel
        if(mainPlayer.getFuel() >= pathCost) {
            // Update grid and player
            grid[playerCoords.getX()][playerCoords.getY()].setType(EMPTY);
            mainPlayer.setCoordinates(destination);
            grid[destination.getX()][destination.getY()].setType(PLAYER);
            grid[destination.getX()][destination.getY()].setValue(mainPlayer);

            fuel -= pathCost;
            mainPlayer.setFuel(fuel);

            int x = destination.getX();
            int y = destination.getY();
            if(grid[x][y].getType() == PLANET)
                startMinigame();
            else if(grid[x][y].getType() == FUEL)
                mainPlayer.setFuel(fuel + 30);
            pathFinder.setStartPosition(mainPlayer.getCoordinates());
            generateMove(path, destination);
        }
    }

    private void generateMove(ArrayList<Coordinates> path, Coordinates destination) {
        ArrayList<Direction> directions = new ArrayList<>();
        ArrayList<Integer> distance = new ArrayList<>();
        Coordinates lastPosition = mainPlayer.getCoordinates();
        Direction currentDir = Direction.NONE;
        int currentDist = 0;

        for(Coordinates c : path) {
            Coordinates moveDir = c.sub(lastPosition);
            lastPosition = c;
            if (moveDir.getX() > 0) {
                if (moveDir.getY() > 0) {
                    if (currentDir.equals(Direction.UP)) {
                        currentDist += 1;
                    } else {
                        directions.add(currentDir);
                        distance.add(currentDist);
                        currentDir = Direction.UP;
                    }
                } else {
                    if (currentDir.equals(Direction.DOWN)) {
                        currentDist += 1;
                    } else {
                        directions.add(currentDir);
                        distance.add(currentDist);
                        currentDir = Direction.DOWN;
                    }
                }
            } else {
                if (moveDir.getY() > 0) {
                    if (currentDir.equals(Direction.RIGHT)) {
                        currentDist += 1;
                    } else {
                        directions.add(currentDir);
                        distance.add(currentDist);
                        currentDir = Direction.RIGHT;
                    }
                } else {
                    if (currentDir.equals(Direction.LEFT)) {
                        currentDist += 1;
                    } else {
                        directions.add(currentDir);
                        distance.add(currentDist);
                        currentDir = Direction.LEFT;
                    }
                }
            }
        }
        BoardMove boardMove = new BoardMove(directions, distance, mainPlayer.getCoordinates(), destination);
        mainPlayer.setBoardMove(boardMove);
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

    public boolean hasReceivedGrid() {
        return receivedGrid;
    }
}