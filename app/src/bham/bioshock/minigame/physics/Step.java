package bham.bioshock.minigame.physics;

import java.util.ArrayList;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Entity;

public class Step {
  public final Position position;
  public final SpeedVector vector;
  private boolean onGround = false;
  private ArrayList<Entity> collides = new ArrayList<>(1);
  
  public Step(Position position, SpeedVector vector)
  {
    this.position = position;
    this.vector = vector;
  }
  
  public void setOnGround(boolean value) {
    onGround = value;
  }
  
  public void addColide(Entity e) {
    collides.add(e);
  }
  
  public boolean getOnGround() {
    return onGround;
  }
  
  public ArrayList<Entity> getCollisions() {
    return collides;
  }

  public void updatePos(Position pos) {
    this.position.x = pos.x;
    this.position.y = pos.y;
  }

}
