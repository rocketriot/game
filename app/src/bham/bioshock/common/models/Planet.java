package bham.bioshock.common.models;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

/** Stores the data of a player on the game board */
public class Planet implements Serializable {

  /** Width of the planet */
  public static final int WIDTH = 3;
  /** Height of the planet */
  public static final int HEIGHT = 3;

  private static final long serialVersionUID = 5775730008817100527L;
  /** ID of the planet */
  private UUID id;
  /** Name of the planet */
  private String name;
  /** Location of the planet */
  private Coordinates coordinates;
  /** If captured, specifies the player that has captured the planet */
  private Player playerCaptured = null;
  /** Stores whether the object has been drawn this cycle */
  private boolean drawn = false;
  /** The texture ID for the object */
  private int textureID;

  private int minigamePlanetRadius;
  private int minigamePlanetGravity;

  public Planet(String name, Coordinates coordinates) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.coordinates = coordinates;
    this.randomiseTexture();
    this.randomMinigameProps();
  }

  public UUID getId() {
    return id;
  }

  private void randomiseTexture() {
    Random r = new Random();
    this.setTextureID(r.nextInt(4));
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

  public int getMinigameTextureId() {
    return textureID + 1;
  }

  public int getMinigameRadius() {
    return this.minigamePlanetRadius;
  }

  public int getMinigameGravity() {
    return this.minigamePlanetGravity;
  }

  public void randomMinigameProps() {
    Random r = new Random();
    this.minigamePlanetRadius = r.nextInt(1500) + 1000;
    this.minigamePlanetGravity = r.nextInt(2000) + 500;
  }
}
