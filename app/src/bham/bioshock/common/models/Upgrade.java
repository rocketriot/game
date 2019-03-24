package bham.bioshock.common.models;

import java.io.Serializable;
import java.util.UUID;

/** Stores the data of an upgrade */
public class Upgrade implements Serializable {
  private static final long serialVersionUID = 5775730008817100527L;

  /** ID of the upgrade */
  private UUID id;

  /** Location of the fuel */
  private Coordinates coordinates;

  /** Stores what actual upgrade the Upgrade contains */
  private Type type;

  public static enum Type {
    FUEL_TANK_SIZE,
    ENGINE_EFFICIENCY,
    FUEL_PER_ROUND,
    BLACKHOLE,
  }

  public Upgrade(Coordinates coordinates) {
    this.id = UUID.randomUUID();
    this.coordinates = coordinates;
    
    generateUpgradeType();
  }

  /** 
   * Returns a random upgrade option 
   * TODO: add different weightings for each upgrade option
   * */
  public void generateUpgradeType() {
    int random = (int) (Math.random() * Type.values().length);
    type = Type.values()[random];
  }

  public UUID getId() {
    return id;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public Type getType() {
    return type;
  }
}
