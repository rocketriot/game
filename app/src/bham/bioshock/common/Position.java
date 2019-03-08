package bham.bioshock.common;

import bham.bioshock.communication.Sendable;

public class Position extends Sendable {

  private static final long serialVersionUID = 1L;
  
  public float x;
  public float y;

  public Position(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public float sqDistanceFrom(Position p) {
    float dx = Math.abs(x - p.x);
    float dy = Math.abs(y - p.y);
    return dx * dx + dy * dy;
  }

}
