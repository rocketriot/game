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

  public static enum Options {

  }

  public Upgrade(Coordinates coordinates) {
    this.id = UUID.randomUUID();
    this.coordinates = coordinates;
  }

  public UUID getId() {
    return id;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  /** 
   * Returns a random upgrade option 
   * TODO: add different weightings for each upgrade option
   * */
  public Upgrade.Options getRandomOption() {
    int random = (int) (Math.random() * Upgrade.Options.values().length);
    return Upgrade.Options.values()[random];
  }
}
