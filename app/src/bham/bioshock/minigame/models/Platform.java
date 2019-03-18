package bham.bioshock.minigame.models;


import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** The type Platform. */
public class Platform extends StaticEntity {

  private static final long serialVersionUID = -2823962935775990161L;

  private TextureRegion texture;

  private Platform parent;

  /**
   * Instantiates a new Platform.
   *
   * @param w the world
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the platform
   * @param height the height of the platform
   */
  public Platform(World w, float x, float y, int width, int height) {
    super(w, x, y, EntityType.PLATFORM);
    this.width = width;
    this.height = height;
    collisionWidth = width;
    collisionHeight = height;
    parent = null;
  }

  /**
   * Instantiates a new Platform.
   *
   * @param w the world
   * @param p the position of the platform
   * @param width the width of the platform
   * @param height the height of the platform
   */
  public Platform(World w, Position p, int width, int height) {
    this(w, p.x, p.y, width, height);
  }

  /**
   * Instantiates a new Platform.
   *
   * @param w the world
   * @param pp the planet position of the platform
   * @param width the width of the platform
   * @param height the height of the platform
   */
  public Platform(World w, PlanetPosition pp, int width, int height) {
    this(w, w.convert(pp), width, height);
  }

  public TextureRegion getTexture() {
    return texture;
  }

  public void load() {
    texture =
        new TextureRegion(new Texture(Gdx.files.internal("app/assets/minigame/platform.png")));
    super.load();
  }

  /**
   * Set parent platform.
   *
   * @param parent the parent platform
   */
  public void setParent(Platform parent) {
    this.parent = parent;
  }

  /**
   * Get parent platform.
   *
   * @return the parent platform
   */
  public Platform getParent() {
    return parent;
  }

  public String toString() {
    return "Platform "+getPlanetPos().toString();
  }

  /**
   * Get the planet position of the left edge of the platform
   * @return the planet position of the left edge of the platform
   */
  public PlanetPosition getLeftEdge() {
    return world.convert(new Position(getX()-(width/2), getY()));
  }
  /**
   * Get the planet position of the right edge of the platform
   * @return the planet position of the right edge of the platform
   */
  public PlanetPosition getRightEdge() {
    return world.convert(new Position(getX()+(width/2), getY()));
  }
}
