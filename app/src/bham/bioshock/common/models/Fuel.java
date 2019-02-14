package bham.bioshock.common.models;

import java.io.Serializable;
import java.util.UUID;

/** Stores the data of a fuel box */
public class Fuel implements Serializable {
  private static final long serialVersionUID = 5775730008817100527L;

  /** ID of the fuel */
  private UUID id;

  /** Location of the fuel */
  private Coordinates coordinates;

  /** The amount of fuel that the fuel box holds */
  private float value = 20.0f;

  /** Stores whether the object has been drawn this cycle */
  private boolean drawn = false;

  public Fuel(Coordinates coordinates) {
    this.id = UUID.randomUUID();
    this.coordinates = coordinates;
  }

  public UUID getId() {
    return id;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
  }

  public boolean isDrawn() {
    return drawn;
  }

  public void setDrawn(boolean drawn) {
    this.drawn = drawn;
  }

}
