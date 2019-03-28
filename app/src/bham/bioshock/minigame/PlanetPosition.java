package bham.bioshock.minigame;

/** The type Planet position. */
public class PlanetPosition {

  /** The Angle the position is at from. */
  public float angle;
  /** The distance from the center of the planet. */
  public float fromCenter;

  /**
   * Instantiates a new Planet position.
   *
   * @param angle the angle of the new position
   * @param fromCenter the distance from the center of the planet of the new position
   */
  public PlanetPosition(float angle, float fromCenter) {
    this.angle = angle;
    this.fromCenter = fromCenter;
  }

  public String toString() {
    return "distance from center: " + fromCenter + " angle: " + angle;
  }
}
