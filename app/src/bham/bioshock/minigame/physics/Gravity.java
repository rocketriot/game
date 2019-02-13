package bham.bioshock.minigame.physics;

import bham.bioshock.minigame.World;

public class Gravity {

  public static double getAngleTo(double x, double y) {
    double dx = x - World.GRAVITY_POS.x;
    double dy = y - World.GRAVITY_POS.y;
    double length = Math.sqrt(dx * dx + dy * dy);

    if (dy > 0) {
      return Math.toDegrees(Math.asin(dx / length));
    } else {
      return 180 - Math.toDegrees(Math.asin(dx / length));
    }
  }
}
