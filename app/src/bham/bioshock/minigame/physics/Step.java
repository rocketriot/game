package bham.bioshock.minigame.physics;

import bham.bioshock.common.Position;

public class Step {
  public final Position position;
  public final SpeedVector vector;
  
  public Step(Position position, SpeedVector vector)
  {
    this.position = position;
    this.vector = vector;
  }
}
