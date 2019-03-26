package bham.bioshock.minigame.models;

import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Static entity is an object rendered in the minigame which is not influenced by gravity and
 * doesn't change it's position after collision.
 */
public abstract class StaticEntity extends Entity {

  private static final long serialVersionUID = 5758318914150228543L;

  /**
   * Constructs new static entity
   * 
   * @param w
   * @param x
   * @param y
   * @param type
   */
  public StaticEntity(World w, float x, float y, EntityType type) {
    super(w, x, y, true, type);
  }

  /**
   * Get texture for rendering
   */
  public abstract TextureRegion getTexture();

  /**
   * Set new position and update collision boundary
   * 
   * @param x
   * @param y
   */
  public void setPosition(float x, float y) {
    this.pos.x = x;
    this.pos.y = y;
    this.collisionBoundary.update(this.pos, getRotation());
  }

}
