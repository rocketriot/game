package bham.bioshock.minigame.worlds;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.assets.Assets.GamePart;
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
import java.util.Random;

/** The type World. */
public abstract class World implements Serializable {

  private static final long serialVersionUID = 4046769956963960819L;
  static Texture texture;
  static Texture frontTexture;
  private static float textureOffset = 0.265f;
  protected int textureId;
  double planetRadius = 1000;
  double gravity = 1500;

  /**
   * Load textures from the asset manager
   *
   * @param manager
   * @param id
   */
  public static void loadTextures(AssetContainer manager, int id) {
    manager.load(Assets.planetBase + id + ".png", Texture.class, GamePart.MINIGAME);
    if (id == 4) {
      manager.load(Assets.planetBase + "4_front.png", Texture.class, GamePart.MINIGAME);
    }
  }

  /**
   * Create textures using asset manager
   *
   * @param manager
   * @param id
   */
  public static void createTextures(AssetContainer manager, int id) {
    texture = manager.get(Assets.planetBase + id + ".png", Texture.class);
    if (id == 4) {
      textureOffset = 0.385f;
      frontTexture = manager.get(Assets.planetBase + "4_front.png", Texture.class);
    } else {
      textureOffset = 0.265f;
    }
  }

  /** Draws planet texture on the screen */
  public void draw(SpriteBatch batch) {
    if (texture == null) return;
    float offset = (float) (textureOffset * getPlanetRadius());
    float radius = (float) getPlanetRadius() + offset;
    batch.draw(texture, -radius, -radius, radius * 2, radius * 2);
  }

  /**
   * Called after main drawing, used to draw something on top of other objects
   *
   * @param batch
   */
  public void afterDraw(SpriteBatch batch) {
    if (textureId == 4) {
      if (frontTexture == null) return;
      batch.begin();
      float offset = (float) (textureOffset * getPlanetRadius());
      float radius = (float) getPlanetRadius() + offset;
      batch.draw(frontTexture, -radius, -radius, radius * 2, radius * 2);
      batch.end();
    }
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

  public abstract void init();

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
   * Calculate angle ratio - in the distance R from the planet center Used to calculate angle given
   * the length (X) distance
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
  public abstract double getPlanetRadius();

  public void setPlanetRadius(int minigameRadius) {
    this.planetRadius = minigameRadius;
  }

  /**
   * Gets gravity.
   *
   * @return the gravity
   */
  public abstract double getGravity();

  public void setGravity(int gravity) {
    this.gravity = gravity;
  }

  /**
   * Get player positions.
   *
   * @return the positions
   */
  public abstract Position[] getPlayerPositions();

  /**
   * Gravity center position.
   *
   * @return the position
   */
  public abstract Position gravityCenter();

  /**
   * Gets rockets.
   *
   * @return the rockets
   */
  public abstract ArrayList<Rocket> getRockets();

  /** Spawns guns. */
  public abstract void spawnGuns();

  /**
   * Gets guns.
   *
   * @return the guns
   */
  public abstract ArrayList<Gun> getGuns();

  /** Spawns platforms */
  public abstract void spawnPlatforms();

  /**
   * Gets platforms.
   *
   * @return the platforms
   */
  public abstract ArrayList<Platform> getPlatforms();

  /**
   * Method to get the platform path to a platform inclusive
   *
   * @param platform the platform you want a path to
   * @return the path
   */
  public abstract ArrayList<Platform> getPlatformPath(Platform platform);

  /**
   * Get texture Id
   *
   * @return
   */
  public int getTextureId() {
    return textureId;
  }

  /**
   * Get random position near the planet
   *
   * @return position
   */
  public Position getRandomPosition() {
    Random r = new Random();
    int angle = r.nextInt(360);
    float distance = (float) (getPlanetRadius() + 2000);

    PlanetPosition pp = new PlanetPosition(angle, distance);
    return convert(pp);
  }

  public void setPlanetTexture(int minigameTextureId) {
    textureId = minigameTextureId;
  }
}
