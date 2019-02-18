package bham.bioshock.minigame.physics;

import bham.bioshock.minigame.worlds.World;

public class Gravity {

  private World world;

  public Gravity(World w) {
    this.world = w;
  }

  public double getAngleTo(double x, double y) {
    double worldX = world.gravityCenter().x;
    double worldY = world.gravityCenter().y;
    return Vector.angleBetween(worldX, worldY, x, y);
  }
}
