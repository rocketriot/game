package bham.bioshock.common.models;

import java.io.Serializable;
import java.util.ArrayList;
import bham.bioshock.communication.Sendable;

/** Stores x and y coordinates */
public class Coordinates extends Sendable {
  private static final long serialVersionUID = 5775730008817100527L;

  private int x;
  private int y;

  public Coordinates(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public ArrayList<Coordinates> getNearby() {
    ArrayList<Coordinates> c = new ArrayList<>();
    c.add(new Coordinates(x - 1, y));
    c.add(new Coordinates(x, y - 1));
    c.add(new Coordinates(x + 1, y));
    c.add(new Coordinates(x, y + 1));
    return c;
  }

  public Boolean isEqual(Coordinates toCheck) {
    return x == toCheck.getX() && y == toCheck.getY();
  }

  public Coordinates difference(Coordinates toSub) {
    return (new Coordinates(this.x - toSub.getX(), this.y - toSub.getY()));
  }

  /**
   * Calculates the distance between this coordinate and another
   * @param point the coordinate being compared to this object
   * @return the distance between the point and this object
   */
  public float calcDistance(Coordinates point) {
    Coordinates diff = this.difference(point);
    return (Math.abs(diff.x) + Math.abs(diff.y));
  }

  public String toString() {
    return "X: " + x + " , Y: " + y;
  }
}
