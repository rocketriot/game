package bham.bioshock.common;

import java.io.Serializable;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.worlds.World;

public class Position implements Serializable {

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
  
  public PositionMove move(World world) {
    return new PositionMove(world, this);
  }

  public Position copy() {
    return new Position(x, y);
  }
  
  public class PositionMove {
    private World world;
    private PlanetPosition pp;
    
    public PositionMove(World w, Position p) {
      this.world = w;
      this.pp = w.convert(p);
    }
    
    public PositionMove up(float distance) {
      pp.fromCenter += distance;
      return this;
    }
    
    public PositionMove down(float distance) {
      pp.fromCenter -= distance;
      return this;
    }
    
    public PositionMove moveLeft(float diff) {
      pp.angle -= diff;
      pp.angle = (pp.angle + 360) % 360;
      return this;
    }
    
    public PositionMove moveRight(float diff) {
      pp.angle += diff;
      pp.angle = pp.angle % 360;
      return this;
    }
    
    public Position pos() {
      return world.convert(pp);
    }
    
    public PlanetPosition ppos() {
      return pp;
    }
    
  }

}
