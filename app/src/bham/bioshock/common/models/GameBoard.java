package bham.bioshock.common.models;

import bham.bioshock.common.consts.GridPoint;
import java.util.Random;

/**
 * Stores the data required for the main game board
 */
public class GameBoard {
    /**
     * A grid containing the locations of all the planets, players, fuel boxes etc
     */
    private GridPoint[][] grid = new GridPoint[36][36];

    /** A list of players */
    private Player[] players;

    /**
     * The ID of the player that the client is controlling, only used client-side
     */
    private int playerId;

    public GameBoard(Player[] players) {
        this.players = players;
    }

    /** Generates a grid with randomly positioned entities */
    public GridPoint[][] generateGrid() throws Exception {
        if (players.length != 4) {
            throw new Exception("NotEnoughPlayers");
        }

        grid[1][1] = new GridPoint(GridPoint.Type.PLAYER, players[0]);
        grid[1][34] = new GridPoint(GridPoint.Type.PLAYER, players[1]);
        grid[34][34] = new GridPoint(GridPoint.Type.PLAYER, players[2]);
        grid[34][1] = new GridPoint(GridPoint.Type.PLAYER, players[3]);

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // Check if point was already generated
                if (grid[i][j] == null) {
                    generateGridPoint(i, j);
                }

                System.out.println(grid[i][j].getType());
            }
            System.out.println("-----");
        }

        // players - exact pos on each corner

        return grid;
    }

    /** Sets a point to a random entity */
    private void generateGridPoint(int x, int y) {
        float randomFloat = (new Random()).nextFloat();

        // Generate empty tile as the default value
        grid[x][y] = new GridPoint(GridPoint.Type.EMPTY);

        // Generate a fuel box
        if (randomFloat <= 0.015) {
            Fuel fuel = new Fuel(new Coordinates(0, 0));
            grid[x][y] = new GridPoint(GridPoint.Type.FUEL, fuel);

            return;
        }

        // Generate a planet
        // Check if able to fit a planet in the grid
        if (randomFloat <= 0.030 && x < 34 && y < 34) {
            // Check if the planet will overwrite an asteroid
            if (grid[x + 2][y] == null && grid[x][y + 2] == null && grid[x + 2][y + 2] == null) {
                // Create a new planet
                Planet planet = new Planet("test", new Coordinates(x, y));

                // Add the planet to the 3x3 space it takes up on the grid
                for (int i = x; i < x + 3; i++)
                    for (int j = y; j < y + 3; j++)
                        grid[i][j] = new GridPoint(GridPoint.Type.PLANET, planet);
            }

            return;
        }

        // Generate an asteroid
        // Check if able to fit an asteroid in the grid
        if (randomFloat <= 0.04 && x < 34 && y < 33) {
            // Check if the asteroid will overwrite an planet
            if (grid[x + 3][y] == null && grid[x][y + 2] == null && grid[x + 2][y + 3] == null) {
                // Add the asteroid to the 4x3 space it takes up on the grid
                for (int i = x; i < x + 3; i++)
                    for (int j = y; j < y + 4; j++)
                        grid[i][j] = new GridPoint(GridPoint.Type.ASTEROID);
            }
        }
    }

    public GridPoint[][] getGrid() {
        return grid;
    }

    public void setGrid(GridPoint[][] grid) {
        this.grid = grid;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}