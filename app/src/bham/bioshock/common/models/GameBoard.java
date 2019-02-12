package bham.bioshock.common.models;

import bham.bioshock.common.consts.GridPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/** Stores the data required for the main game board */
public class GameBoard implements Serializable {

  private static final long serialVersionUID = 5775730008817100527L;

  public final int GRID_SIZE = 36;

  /** A grid containing the locations of all the planets, players, fuel boxes etc */
  private GridPoint[][] grid;

  /** Generates a grid with randomly positioned entities */
  public GridPoint[][] generateGrid(ArrayList<Player> players) throws Exception {
    // Make sure there are 4 players
    if (players.size() != 4) {
      throw new Exception("NotEnoughPlayers");
    }

    // initialize grid
    grid = new GridPoint[GRID_SIZE][GRID_SIZE];

    // Updates coordinates in player
    players.get(0).setCoordinates(new Coordinates(0, 0));
    players.get(1).setCoordinates(new Coordinates(0, GRID_SIZE - 1));
    players.get(2).setCoordinates(new Coordinates(GRID_SIZE - 1, GRID_SIZE - 1));
    players.get(3).setCoordinates(new Coordinates(GRID_SIZE - 1, 0));

    // Add the players to the board
    grid[0][0] = new GridPoint(GridPoint.Type.PLAYER, players.get(0));
    grid[0][GRID_SIZE - 1] = new GridPoint(GridPoint.Type.PLAYER, players.get(1));
    grid[GRID_SIZE - 1][GRID_SIZE - 1] = new GridPoint(GridPoint.Type.PLAYER, players.get(2));
    grid[GRID_SIZE - 1][0] = new GridPoint(GridPoint.Type.PLAYER, players.get(3));

    // Go through each point and generate it's type
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        // Check if point was already generated
        if (grid[i][j] == null) {
          generateGridPoint(i, j);
        }
      }
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
    if (randomFloat <= 0.030 && x < GRID_SIZE - 2 && y < GRID_SIZE - 2) {
      // Check if the planet will overwrite an asteroid
      if (grid[x + 2][y] == null && grid[x][y + 2] == null && grid[x + 2][y + 2] == null) {
        // Create a new planet
        Planet planet = new Planet("test", new Coordinates(x, y));

        // Add the planet to the 3x3 space it takes up on the grid
        for (int i = x; i < x + 3; i++)
          for (int j = y; j < y + 3; j++) grid[i][j] = new GridPoint(GridPoint.Type.PLANET, planet);
      }

      return;
    }

    // Generate an asteroid
    // Check if able to fit an asteroid in the grid
    if (randomFloat <= 0.04 && x < GRID_SIZE - 3 && y < GRID_SIZE - 4) {
      // Check if the asteroid will overwrite an planet
      if (grid[x + 3][y] == null && grid[x][y + 2] == null && grid[x + 2][y + 3] == null) {
        // Create a new asteroid
        Asteroid asteroid = new Asteroid("test", new Coordinates(x, y));

        // Add the asteroid to the 4x3 space it takes up on the grid
        for (int i = x; i < x + 3; i++)
          for (int j = y; j < y + 4; j++)
            grid[i][j] = new GridPoint(GridPoint.Type.ASTEROID, asteroid);
      }
    }
  }

  public GridPoint[][] getGrid() {
    return grid;
  }

  public void setGrid(GridPoint[][] grid) {
    this.grid = grid;
  }
}
