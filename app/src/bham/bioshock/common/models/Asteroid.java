package bham.bioshock.common.models;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

/** Stores the data of an asteroid on the game board */
public class Asteroid implements Serializable {

  /** Width of the asteroid */
  public static final int WIDTH = 3;
  /** Height of the asteroid */
  public static final int HEIGHT = 4;

  private static final long serialVersionUID = 5775730008817100527L;
  /** ID of the planet */
  private UUID id;
  /** Name of the asteroid */
  private String name;
  /** Location of the asteroid */
  private Coordinates coordinates;
  /** If captured, specifies the player that has captured the asteroid */
  private Player playerCaptured = null;
  /** Stores whether the object has been drawn this cycle */
  private boolean drawn = false;
  /** The texture ID for the object */
  private int textureID;

  public Asteroid(String name, Coordinates coordinates) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.coordinates = coordinates;
    this.randomiseTexture();
  }

  public UUID getId() {
    return id;
  }

  private void randomiseTexture() {
    Random r = new Random();
    this.setTextureID(r.nextInt(5));
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  public Player getPlayerCaptured() {
    return playerCaptured;
  }

  public void setPlayerCaptured(Player playerCaptured) {
    this.playerCaptured = playerCaptured;
  }

  public boolean isDrawn() {
    return drawn;
  }

  public void setDrawn(boolean drawn) {
    this.drawn = drawn;
  }

  public int getTextureID() {
    return textureID;
  }

  public void setTextureID(int textureID) {
    this.textureID = textureID;
  }
}
