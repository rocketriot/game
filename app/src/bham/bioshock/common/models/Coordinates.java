package bham.bioshock.common.models;

import java.io.Serializable;

/** Stores x and y coordinates */
public class Coordinates implements Serializable {
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

  public Boolean isEqual(Coordinates toCheck) {
    return x == toCheck.getX() && y == toCheck.getY();
  }
}
