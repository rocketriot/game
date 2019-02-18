package bham.bioshock.minigame.physics;

import bham.bioshock.minigame.worlds.World;

public class Gravity {

  private World world;
  
  public Gravity(World w) {
    this.world = w;
  }
  
  public double getAngleTo(double x, double y) {
    double dx = x - world.gravityCenter().x;
    double dy = y - world.gravityCenter().y;
    double length = Math.sqrt(dx * dx + dy * dy);

    if (dy > 0) {
      return Math.toDegrees(Math.asin(dx / length));
    } else {
      return 180 - Math.toDegrees(Math.asin(dx / length));
    }
  }
}
