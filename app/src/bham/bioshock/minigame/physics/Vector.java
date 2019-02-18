package bham.bioshock.minigame.physics;

public class Vector {
  public double dx;
  public double dy;

  public Vector(double dx, double dy) {
    this.dx = dx;
    this.dy = dy;
  }
  
  public static double angleBetween(double x1, double y1, double x2, double y2) {
    double dx = x2 - x1;
    double dy = y2 - y1;
    double length = Math.sqrt(dx * dx + dy * dy);

    if (dy > 0) {
      return Math.toDegrees(Math.asin(dx / length));
    } else {
      return 180 - Math.toDegrees(Math.asin(dx / length));
    }
  }
}
