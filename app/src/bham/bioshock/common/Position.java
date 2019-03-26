package bham.bioshock.common;

import java.io.Serializable;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.worlds.World;

public class Position implements Serializable {

  private static final long serialVersionUID = 1L;
  
  public float x;
  public float y;

  /**
   * Creates new position in Euclidean space
   * @param x
   * @param y
   */
  public Position(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Squared distance between two points
   * 
   * @param p
   * @return
   */
  public float sqDistanceFrom(Position p) {
    float dx = Math.abs(x - p.x);
    float dy = Math.abs(y - p.y);
    return dx * dx + dy * dy;
  }
  
  /**
   * Creates a copy of the position
   * @return
   */
  public Position copy() {
    return new Position(x, y);
  }
  
  /**
   * Starts a move of the position
   * returns PositionMove object which can be modified to in terms of planet position
   * 
   * @param world
   * @return
   */
  public PositionMove move(World world) {
    return new PositionMove(world, this);
  }
  
  @Override
  public String toString() {
    return "x: " + x + " y: " + y;
  }
  
  /**
   * Moving position
   */
  public class PositionMove {
    private World world;
    private PlanetPosition pp;
    
    /**
     * Creates new position move for the world
     * 
     * @param w
     * @param p
     */
    public PositionMove(World w, Position p) {
      this.world = w;
      this.pp = w.convert(p);
    }
    
    /**
     * Increase distance from planet
     */
    public PositionMove up(float distance) {
      pp.fromCenter += distance;
      return this;
    }
    
    /**
     * Change angle
     */
    public PositionMove moveV(float diff) {
      pp.angle += diff;
      pp.angle = pp.angle % 360;
      return this;
    }
    
    /**
     * Convert back to position in Euclidean space
     * @return
     */
    public Position pos() {
      return world.convert(pp);
    }
    
    /**
     * Convert back to position in Polar space
     * @return
     */
    public PlanetPosition ppos() {
      return pp;
    }
  }

}
