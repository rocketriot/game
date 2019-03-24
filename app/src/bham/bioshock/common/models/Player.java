package bham.bioshock.common.models;

import bham.bioshock.common.Direction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/** Stores the data of a player on the game board */
public class Player implements Serializable {

  private static final long serialVersionUID = 5775730008817100527L;

  /** ID of the player */
  private UUID id;

  /** Username of the player */
  private String username;

  /** Location of the player */
  private Coordinates coordinates;

  /** The amount of fuel the player has left */
  private float fuel = 100.0f;

  /** The number of planets the player has captured */
  private int planetsCaptured = 0;

  /** Specifies if the player is controlled by AI */
  private boolean isCpu = false;

  /** Player's textureID */
  private int textureID;

  /** The number of points the player has */
  private int points = 0;

  /** Which way the player is facing */
  private int rotate = -1;

  /** Object containing infomation about a players move */
  private ArrayList<Move> boardMove;

  /** The coordinates at which the player first spawns on the gameboard */
  private Coordinates spawnPoint;

  /** The fuel cost for moving one grid space */
  public static final float FUEL_GRID_COST = 10f;

  /** The maximum amount of fuel a player hold at one time */
  public static final float MAX_FUEL = 100f;

  public Player(UUID id, String username, Boolean isCpu) {
    this.id = id;
    this.username = username;
    this.isCpu = isCpu;
    this.textureID = 0;
  }
  
  public Player(String username, boolean isCpu) {
    this(UUID.randomUUID(), username, isCpu);
  }

  /** Sets the players coordinates to their original spawn coordinates */
  public void moveToSpawn() {
    setCoordinates(spawnPoint);
  }
  
  public Player(String username) {
    this(username, false);
  }

  public UUID getId() {
    return id;
  }

  public boolean isCpu() {
    return isCpu;
  }

  public void setCpu(boolean cpu) {
    isCpu = cpu;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  public float getFuel() {
    return fuel;
  }

  public void setFuel(float fuel) {
    this.fuel = fuel;
  }

  public void increaseFuel(float fuel) {
    this.fuel = Math.min(this.fuel + fuel, MAX_FUEL);
  }

  public void decreaseFuel(float fuel) {
    this.fuel = Math.max(this.fuel - fuel, 0f);
  }

  public int getPlanetsCaptured() {
    return planetsCaptured;
  }

  public void setPlanetsCaptured(int planetsCaptured) {
    this.planetsCaptured = planetsCaptured;
  }

  public int getPoints() {
    return points;
  }

  public void addPoints(int points){
    this.points += points;
  }
  
  public void setPoints(int points) {
    this.points = points;
  }

  public ArrayList<Move> getBoardMove() {
    return boardMove;
  }

  public void clearBoardMove() {
    boardMove = null;
  }

  public void createBoardMove(ArrayList<Coordinates> path) {
    boardMove = new ArrayList<>();

    // Add starting position
    boardMove.add(new Move(Direction.NONE, path.get(0)));

    for (int i = 0; i < path.size() - 1; i++) {
      Coordinates coordinates = path.get(i);
      Coordinates nextCoordinates = path.get(i + 1);

      // Calculate the difference between the coordinates
      Coordinates difference = nextCoordinates.difference(coordinates);

      // Handle when X is unchanged
      if (difference.getX() == 0) {
        boardMove.add(new Move(difference.getY() < 0 ? Direction.DOWN : Direction.UP, nextCoordinates));
      }
      
      // Handle when Y is unchanged
      if (difference.getY() == 0) {
        boardMove.add(new Move(difference.getX() < 0 ? Direction.LEFT : Direction.RIGHT, nextCoordinates));
      }
    }
  }
  
  @Override
  public boolean equals(Object o) { 
      // If the object is compared with itself then return true   
      if (o == this) { 
          return true; 
      }
      if (!(o instanceof Player)) { 
          return false; 
      } 
      Player p = (Player) o; 
      return this.id.equals(p.getId());
  }

  public Coordinates getSpawnPoint() {
    return spawnPoint;
  }

  public void setSpawnPoint(Coordinates spawnPoint) {
    this.spawnPoint = spawnPoint;
  }

  public class Move implements Serializable {
    private Direction direction;
    private Coordinates coordinates;

    private static final long serialVersionUID = 5775730008817100526L;

    public Move(Direction direction, Coordinates coordinates) {
      this.direction = direction;
      this.coordinates = coordinates;
    }

    public Direction getDirection() {
      return direction;
    }

    public Coordinates getCoordinates() {
      return coordinates;
    }
  }

  public int getTextureID() {
    return textureID;
  }

  public void setTextureID(int textureID) {
    this.textureID = textureID;
  }
}
