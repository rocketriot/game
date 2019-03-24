package bham.bioshock.common.models;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

/** Stores the data of a black hole on the game board */
public class BlackHole implements Serializable {
  private static final long serialVersionUID = 5775730008817100527L;
  
  /** Width of the black hole */
  public static final int WIDTH = 3;
  
  /** Height of the black hole */
  public static final int HEIGHT = 4;

  /** ID of the black hole */
  private UUID id;
  
  /** Location of the black hole */
  private Coordinates coordinates;

  /** The texture ID for the object */
  private int textureID;

  public BlackHole(Coordinates coordinates) {
    this.id = UUID.randomUUID();
    this.coordinates = coordinates;
    this.randomiseTexture();
  }

  public UUID getId() {
    return id;
  }

  private void randomiseTexture() {
    Random r = new Random();
    textureID = r.nextInt(3);
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public int getTextureID() {
    return textureID;
  }
}
