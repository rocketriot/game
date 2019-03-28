package bham.bioshock.common.models;

import java.io.Serializable;
import java.util.UUID;

/** Stores the data of a black hole on the game board */
public class BlackHole implements Serializable {
  /** Width of the black hole */
  public static final int WIDTH = 3;
  /** Height of the black hole */
  public static final int HEIGHT = 3;

  private static final long serialVersionUID = 5775730008817100527L;
  /** ID of the black hole */
  private UUID id;

  /** Location of the black hole */
  private Coordinates coordinates;

  public BlackHole(Coordinates coordinates) {
    this.id = UUID.randomUUID();
    this.coordinates = coordinates;
  }

  public UUID getId() {
    return id;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }
}
