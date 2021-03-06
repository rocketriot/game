package bham.bioshock.common.models;

import bham.bioshock.common.Direction;
import bham.bioshock.common.models.Upgrade.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/** Stores the data of a player on the game board */
public class Player implements Serializable {

  /** The maximum amount of fuel a player hold at one time */
  public static final float BASE_MAX_FUEL = 100f;
  /** The fuel cost for moving one grid space */
  public static final float FUEL_GRID_COST = 10f;
  /** Points gained per round for each planet owned */
  public static final int POINTS_PER_PLANET = 100;
  /** Fuel gained per round */
  public static final float FUEL_PER_ROUND = 50f;

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
  /** The textual description of the last upgrade to be picked up by the player */
  private String lastUpgradeText = null;
  /** The coordinates the player will teleport to if they go through a black hole */
  private Coordinates randomCoords;

  private boolean isAddingBlackHole = false;

  private ArrayList<Upgrade.Type> upgrades = new ArrayList<>();

  public Player(UUID id, String username, Boolean isCpu) {
    this.id = id;
    this.username = username;
    this.isCpu = isCpu;
    this.textureID = 0;
  }

  public Player(String username, boolean isCpu) {
    this(UUID.randomUUID(), username, isCpu);
  }

  public Player(String username) {
    this(username, false);
  }

  /** Sets the players coordinates to their original spawn coordinates */
  public void moveToSpawn() {
    if (spawnPoint != null) {
      setCoordinates(spawnPoint);
    }
  }

  public float getFuelGridCost() {
    float modifier = 1;
    if (hasUpgrade(Type.ENGINE_EFFICIENCY)) {
      modifier -= 0.2;
    }
    return FUEL_GRID_COST * modifier;
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

  /** Returns the maximum fuel a player has after modifiers e.g. upgrades or planets owned */
  public float getMaxFuel() {
    float modifier = 0;
    if (hasUpgrade(Type.FUEL_TANK_SIZE)) {
      modifier += 50;
    }
    modifier += planetsCaptured * 20;
    return BASE_MAX_FUEL + modifier;
  }

  public void increaseFuel(float fuel) {
    this.fuel = Math.min(this.fuel + fuel, getMaxFuel());
  }

  public void decreaseFuel(float fuel) {
    this.fuel = Math.max(this.fuel - fuel, 0f);
  }

  public void addUpgrade(Upgrade upgrade) {
    if (hasUpgrade(upgrade.getType()) && !(upgrade.getType().equals(Type.BLACK_HOLE))) {
      increaseFuel(30);
      lastUpgradeText = "Upgrade already owned";
    } else {
      upgrades.add(upgrade.getType());
      lastUpgradeText = upgrade.getDisplayName();
    }
  }

  public ArrayList<Upgrade.Type> getUpgrades() {
    return upgrades;
  }

  public boolean hasUpgrade(Upgrade.Type type) {
    return upgrades.contains(type);
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

  public void setPoints(int points) {
    this.points = points;
  }

  public void addPoints(int points) {
    this.points += points;
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
        boardMove.add(
            new Move(difference.getY() < 0 ? Direction.DOWN : Direction.UP, nextCoordinates));
      }

      // Handle when Y is unchanged
      if (difference.getY() == 0) {
        boardMove.add(
            new Move(difference.getX() < 0 ? Direction.LEFT : Direction.RIGHT, nextCoordinates));
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

  /** Handles changes to player when a new round begins */
  public void newRound() {
    addPoints(planetsCaptured * POINTS_PER_PLANET);
    increaseFuel(FUEL_PER_ROUND);
    if (hasUpgrade(Type.FUEL_PER_ROUND)) {
      increaseFuel(10f);
    }
  }

  public String getLastUpgradeText() {
    return lastUpgradeText;
  }

  public void setLastUpgradeText(String lastUpgradeText) {
    this.lastUpgradeText = lastUpgradeText;
  }

  public void setTeleportCoords(Coordinates randomCoords) {
    this.randomCoords = randomCoords;
  }

  public Coordinates getRandomCoords() {
    return randomCoords;
  }

  public int getTextureID() {
    return textureID;
  }

  public void setTextureID(int textureID) {
    this.textureID = textureID;
  }

  public boolean isAddingBlackHole() {
    return isAddingBlackHole;
  }

  public void toggleAddingBlackHole() {
    isAddingBlackHole = !isAddingBlackHole;
  }

  public void addedBlackHole() {
    isAddingBlackHole = false;
    upgrades.remove(Upgrade.Type.BLACK_HOLE);
  }

  public class Move implements Serializable {
    private static final long serialVersionUID = 5775730008817100526L;
    private Direction direction;
    private Coordinates coordinates;

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
}
