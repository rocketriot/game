package bham.bioshock.minigame.worlds;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.physics.Vector;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type World.
 */
abstract public class World implements Serializable {

  private static final long serialVersionUID = 4046769956963960819L;
  protected int textureOffset = 530;

  /**
   * Draws planet texture on the screen
   * 
   * @param batch
   */
  public void draw(SpriteBatch batch) {
    float radius = (float) getPlanetRadius()+textureOffset;
    batch.draw(getTexture(), -radius, -radius, radius*2, radius*2);
  }
  
  /**
   * Gets angle to an x and y coordinate.
   *
   * @param x the x
   * @param y the y
   * @return the angle
   */
  public double getAngleTo(double x, double y) {
    double worldX = gravityCenter().x;
    double worldY = gravityCenter().y;
    double angle = Vector.angleBetween(worldX, worldY, x, y);
    return angle < 0 ? angle + 360 : angle;
  }

  /**
   * Gets distance to an x and y coordinate.
   *
   * @param x the x
   * @param y the y
   * @return the distance
   */
  public double getDistanceTo(double x, double y) {
    double dx = x - gravityCenter().x;
    double dy = y - gravityCenter().y;

    return Math.sqrt(dx * dx + dy * dy);
  }

  /**
   * Gets distance from ground to an x and y coordinate.
   *
   * @param x the x
   * @param y the y
   * @return the distance
   */
  public double fromGroundTo(double x, double y) {
    return getDistanceTo(x, y) - getPlanetRadius();
  }

  /**
   * Convert position to planet position.
   *
   * @param p the position
   * @return the planet position
   */
  public PlanetPosition convert(Position p) {
    return new PlanetPosition((float) getAngleTo(p.x, p.y), (float) getDistanceTo(p.x, p.y));
  }

  /**
   * Calculate angle ratio - in the distance R from the planet center
   * Used to calculate angle given the length (X) distance
   *
   * @param r the ratio
   * @return ratio between pixels and angle
   */
  public double angleRatio(double r) {
    return 180 / (Math.PI * r);
  }

  /**
   * Convert planet position to position.
   *
   * @param p the planet position
   * @return the position
   */
  public Position convert(PlanetPosition p) {
    double radians = Math.toRadians(p.angle);
    double dx = Math.sin(radians) * p.fromCenter;
    double dy = Math.cos(radians) * p.fromCenter;
    float x = (float) dx + gravityCenter().x;
    float y = (float) dy + gravityCenter().y;
    return new Position(x, y);
  }

  /**
   * Gets planet radius.
   *
   * @return the planet radius
   */
  abstract public double getPlanetRadius();

  /**
   * Gets gravity.
   *
   * @return the gravity
   */
  abstract public double getGravity();

  /**
   * Get player positions.
   *
   * @return the positions
   */
  abstract public Position[] getPlayerPositions();

  /**
   * Gravity center position.
   *
   * @return the position
   */
  abstract public Position gravityCenter();

  /**
   * Gets rockets.
   *
   * @return the rockets
   */
  abstract public ArrayList<Rocket> getRockets();

  /**
   * Gets guns.
   *
   * @return the guns
   */
  abstract public ArrayList<Gun> getGuns();

  /**
   * Gets platforms.
   *
   * @return the platforms
   */
  abstract public ArrayList<Platform> getPlatforms();

  /**
   * Gets texture.
   *
   * @return the texture
   */
  abstract public Texture getTexture();

  public abstract void afterDraw(SpriteBatch batch);

}
