package bham.bioshock.common.models;

import java.io.Serializable;
import java.util.UUID;

/**
 * Stores the data of a player on the game board
 */
public class Player implements Serializable {

  private static final long serialVersionUID = 5775730008817100527L;

  /**
   * ID of the player
   */
  private UUID id;

  /**
   * Username of the player
   */
  private String username;

  /**
   * Location of the player
   */
  private Coordinates coordinates;

  /**
   * The amount of fuel the player has left
   */
  private float fuel = 100.0f;

  /**
   * The number of planets the player has captured
   */
  private int planetsCaptured = 0;

  /**
   * Specifies if the player is controlled by AI
   */
  private boolean isCpu = false;

  /**
   * Player's textureID
   */
  private int textureID;

  /**
   * The number of points the player has
   */
  private int points = 0;

  /**
   * Object containing infomation about a players move
   */
  private BoardMove boardMove;

  public Player() {
    this.id = UUID.randomUUID();
    this.textureID = 0;
  }

  public Player(String username) {
    this();
    this.username = username;
  }

  public Player(String username, boolean isCpu) {
    this();
    this.username = username;
    this.isCpu = isCpu;
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

  public BoardMove getBoardMove() {
    return boardMove;
  }

  public void setBoardMove(BoardMove boardMove) {
    this.boardMove = boardMove;
  }

  public int getTextureID() {
    return textureID;
  }

  public void setTextureID(int textureID) {
    this.textureID = textureID;
  }
}
