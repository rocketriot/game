package bham.bioshock.minigame.models;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.assets.Assets.GamePart;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Gun object can be captured by the player and held in the equipment. Player carrying a gun can
 * shoot to other players
 */
public class Gun extends Entity {

  private static final long serialVersionUID = 7208353527077911911L;

  private static TextureRegion texture;

  /**
   * Creates gun object at specified initial position
   *
   * @param w world
   * @param x position
   * @param y position
   */
  public Gun(World w, float x, float y) {
    super(w, x, y, EntityType.GUN);
    setRotation(0);
    fromGround = -5;
  }

  @Override
  public TextureRegion getTexture() {
    return texture;
  }

  /**
   * Create textures for rendering
   */
  public static void createTextures(AssetContainer manager) {
    texture = new TextureRegion(manager.get(Assets.gun, Texture.class));
  }

  /**
   * Queue textures for loading
   */
  public static void loadTextures(AssetContainer manager) {
    manager.load(Assets.gun, Texture.class, GamePart.MINIGAME);
  }


}
