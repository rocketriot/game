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

  /** Stores the description of this upgrade */
  private String desc;

  /** Stores the display name of the upgrade */
  private String displayName;

  public Upgrade(Coordinates coordinates) {
    this.id = UUID.randomUUID();
    this.coordinates = coordinates;

    generateUpgradeType();
  }

  public static String getTypeDesc(Type type) {
    String desc = "No description found";
    if (type.equals(Type.FUEL_TANK_SIZE)) {
      desc = "Fuel capacity increased by 50";
    } else if (type.equals(Type.ENGINE_EFFICIENCY)) {
      desc = "Fuel costs reduced by 20%";
    } else if (type.equals(Type.FUEL_PER_ROUND)) {
      desc = "Receive 10 extra fuel per round";
    } else if (type.equals(Type.BLACK_HOLE)) {
      desc = "Placeable black hole which teleports players to a random board location";
    } else if (type.equals(Type.MINIGAME_SHIELD)) {
      desc = "Gain shield at the start of each minigame which blocks 4 hearts of damage";
    }
    return desc;
  }

  public static String getTypeDisplayName(Type type) {
    String name = "No name found";
    if (type.equals(Type.FUEL_TANK_SIZE)) {
      name = "Fuel tank upgrade";
    } else if (type.equals(Type.ENGINE_EFFICIENCY)) {
      name = "Engine efficiency upgrade";
    } else if (type.equals(Type.FUEL_PER_ROUND)) {
      name = "Fuel gain upgrade";
    } else if (type.equals(Type.BLACK_HOLE)) {
      name = "Black hole";
    } else if (type.equals(Type.MINIGAME_SHIELD)) {
      name = "Minigame shield";
    }
    return name;
  }

  /** Returns a random upgrade option */
  public void generateUpgradeType() {
    int random = (int) (Math.random() * Type.values().length);
    type = Type.values()[random];
    desc = getTypeDesc(type);
    displayName = getTypeDisplayName(type);
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

  public String getDesc() {
    return desc;
  }

  public String getDisplayName() {
    return displayName;
  }

  public enum Type {
    FUEL_TANK_SIZE,
    ENGINE_EFFICIENCY,
    FUEL_PER_ROUND,
    BLACK_HOLE,
    MINIGAME_SHIELD
  }
}
