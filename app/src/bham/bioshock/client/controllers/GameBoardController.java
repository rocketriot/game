package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.common.models.*;
import bham.bioshock.common.consts.GridPoint;

import java.util.Random;

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

    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }

    public boolean checkDestination(Coordinates currentPosition, Coordinates destination, float fuel){
        if (destination.getX() - currentPosition.getX()  > fuel || destination.getY() - currentPosition.getY() > fuel )
            return false;
        return true;
    }

    public void move(Player player, Coordinates destination){
        Coordinates currentPosition = player.getCoordinates();
        float fuel = player.getFuel();
        if(checkDestination(currentPosition,destination, fuel){
            player.setCoordinates(destination);
            float lostFuel = 0; // will be calculated using pathfinding
            player.setFuel(fuel - lostFuel);

            if(gameBoard.checkIfPlanet(destination))
                startMinigame();
        }
    }

    public void startMinigame(){

    }

    public void miniGameWon(Player player, Planet planet){ planet.setPlayerCaptured(player)};

    public void miniGameLost(Player player, Planet planet){
        float currentFuel = player.getFuel();
        player.setFuel(currentFuel-5);

        int x = new Random().nextInt();
        int y = new Random().nextInt();
        Coordinates newCoordinates = new Coordinates(x,y);

        planet.setCoordinates(newCoordinates);


    }


}