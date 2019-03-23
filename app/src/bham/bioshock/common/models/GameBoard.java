package bham.bioshock.common.models;

import bham.bioshock.common.consts.GridPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/** Stores the data required for the main game board */
public class GameBoard implements Serializable {

  private static final Logger logger = LogManager.getLogger(GameBoard.class);

  private static final long serialVersionUID = 5775730008817100527L;

  public final int GRID_SIZE = 36;
  
  private ArrayList<Planet> planets = new ArrayList<>();

  /** A grid containing the locations of all the planets, players, fuel boxes etc */
  private GridPoint[][] grid;

  /** Generates a grid with randomly positioned entities */
  public GridPoint[][] generateGrid() {
    // initialize grid
    grid = new GridPoint[GRID_SIZE][GRID_SIZE];

    // Go through each point and generate it's type
    for (int x = 0; x < grid.length; x++) {
      for (int y = 0; y < grid[x].length; y++) {
        generateGridPoint(x, y);
      }
    }

    return grid;
  }

  /** Checks is a set of coordinates are a set number of spaces away from a player */
  private boolean isGridPointNearPlayer(int x, int y) {
    // Setup bounds
    int min = 4;
    int max = (GRID_SIZE - min) - 1;

    // Check top left
    if (x < min && y < min) return true;

    // Check top right
    if (x < min && y > max) return true;

    // Check bottom left
    if (x > max && y < min) return true;

    // Check bottom right
    if (x > max && y > max) return true;

    return false;
  }

  public boolean isNextToThePlanet(Coordinates pos) {
    // Currently on the planet
    if (getGridPoint(pos).isType(GridPoint.Type.PLANET)) {
      return true;
    }

    // Next to the planet
    for (Coordinates p : pos.getNearby()) {
      if (pos.getX() > 0 && pos.getX() < GRID_SIZE - 1 && pos.getY() > 0 && pos.getY() < GRID_SIZE - 1) {
        if (getGridPoint(p).isType(GridPoint.Type.PLANET)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * returns the planet object if there is a capturable planet adjacent to the passed in coordinates
   * @param pos the coordinates of the grid point to be checked
   * @param player the player of which to check planet ownership against
   * @return the planet object which is adjacent to that position or null if one does not exist
   */
  public Planet getAdjacentPlanet(Coordinates pos, Player player) {
    // Next to the planet
    for (Coordinates p : pos.getNearby()) {
      if (pos.getX() > 0 && pos.getX() < GRID_SIZE - 1 && pos.getY() > 0 && pos.getY() < GRID_SIZE - 1) {
        if (getGridPoint(p).isType(GridPoint.Type.PLANET)) {
          Planet planet = (Planet) getGridPoint(p).getValue();
          if (planet.getPlayerCaptured() == null || !planet.getPlayerCaptured().equals(player)) {
            return planet;
          }
        }
      }
    }
    return null;
  }

  /** Sets a point to a random entity */
  private void generateGridPoint(int x, int y) {
    // Do nothing if point was already generated
    if (grid[x][y] != null) return;

    // Generate empty tile as the default value
    grid[x][y] = new GridPoint(GridPoint.Type.EMPTY);

    // Don't generate anything if point is near player
    if (isGridPointNearPlayer(x, y)) return;

    float randomFloat = (new Random()).nextFloat();

    // Generate a fuel box
    if (randomFloat <= 0.015) {
      Fuel fuel = new Fuel(new Coordinates(x, y));
      grid[x][y] = new GridPoint(GridPoint.Type.FUEL, fuel);

      return;
    }

    // Generate an upgrade
    if (randomFloat <= 0.02) {
      Upgrade upgrade = new Upgrade(new Coordinates(x, y));
      grid[x][y] = new GridPoint(GridPoint.Type.UPGRADE, upgrade);

      return;
    }

    // Generate a planet
    if (randomFloat <= 0.045) {
      // Check if there's enough space to generate the planet
      if (isEnoughSpace(x, y, Planet.WIDTH, Planet.HEIGHT)) {
        // Create a new planet
        Planet planet = new Planet("test", new Coordinates(x, y));
        planets.add(planet);

        // Add the planet to the 3x3 space it takes up on the grid
        for (int i = x; i < x + 3; i++)
          for (int j = y; j < y + 3; j++) grid[i][j] = new GridPoint(GridPoint.Type.PLANET, planet);
      }

      return;
    }

    // Generate an asteroid
    if (randomFloat <= 0.05) {
      // Check if there's enough space to generate the asteroid
      if (isEnoughSpace(x, y, Asteroid.WIDTH, Asteroid.HEIGHT)) {
        // Create a new asteroid
        Asteroid asteroid = new Asteroid("test", new Coordinates(x, y));

        // Add the asteroid to the 4x3 space it takes up on the grid
        for (int i = x; i < x + 3; i++)
          for (int j = y; j < y + 4; j++)
            grid[i][j] = new GridPoint(GridPoint.Type.ASTEROID, asteroid);
      }
    }
  }

  /** Checks if there is enough space to generated an entity */
  private boolean isEnoughSpace(int x, int y, int width, int height) {
    // Check if within grid bounds
    if (x + width >= GRID_SIZE || y + height >= GRID_SIZE) return false;

    // Go through spaces around entity and check if it's empty
    for (int i = x; i <= x + width; i++) {
      for (int j = y; j <= y + height; j++) {
        // If space is rendered and not EMPTY, there's not space
        if (grid[i][j] != null && !grid[i][j].isType(GridPoint.Type.EMPTY)) return false;
      }
    }

    return true;
  }

  /** Changes a grid point to empty */
  public void removeGridPoint(Coordinates coordinates) {
    int x = coordinates.getX();
    int y = coordinates.getY();
    
    grid[x][y] = new GridPoint(GridPoint.Type.EMPTY);
  }

  public GridPoint[][] getGrid() {
    return grid;
  }

  public void setGrid(GridPoint[][] grid) {
    this.grid = grid;
  }

  public GridPoint getGridPoint(Coordinates coordinates) {
    int x = coordinates.getX();
    int y = coordinates.getY();
    if (x >= 0 && grid.length > x && y >= 0 && grid[x].length > y) {
      return grid[x][y];
    }
    logger.error("No coordinates " + coordinates + " in the grid!");
    return null;
  }
  
  public Planet getPlanet(UUID id) {
    for(Planet p : planets) {
      if(p.getId().equals(id)) {
        return p;
      }
    }
    return null;
  }
}
