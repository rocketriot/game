package bham.bioshock.minigame.models;

import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Goal object represents the final position for the platformer objective
 */
public class Goal extends Entity {

  private static final long serialVersionUID = -8342563560891277870L;

  private static TextureRegion texture;

  /**
   * Creates a new goal at specified position
   * 
   * @param w
   * @param x
   * @param y
   * @param isStatic
   * @param type
   */
  public Goal(World w, float x, float y) {
    super(w, x, y, true, EntityType.GOAL);
    setRotation(0);
  }

  /**
   * Returns texture for rendering
   */
  @Override
  public TextureRegion getTexture() {
    return texture;
  }

  /**
   * Load textures for drawing
   */
  public static void loadTextures() {
    texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/minigame/flag.png")));
  }
}
