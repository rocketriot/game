package bham.bioshock.minigame.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.assets.Assets.GameType;
import bham.bioshock.client.interfaces.AssetContainer;
import bham.bioshock.minigame.worlds.World;
import java.util.HashMap;

/**
 * The type Rocket.
 */
public class Rocket extends Entity {

  private static final long serialVersionUID = 471939014038953098L;

  /**
   * List of textures.
   */
  private static HashMap<Integer, Texture> textures = new HashMap<>();

  /**
   * Which colour the rocket will be.
   */
  private Integer color;

  /**
   * The rocket texture
   */
  private TextureRegion texture;

  /**
   * Instantiates a new Rocket.
   *
   * @param w the world
   * @param x the x coordinate
   * @param y the y coordinate
   * @param color the color
   */
  public Rocket(World w, float x, float y, int color) {
    super(w, x, y, EntityType.ROCKET);
    this.color = color;
    width = 400;
    height = 400;
    collisionHeight = 400;
    collisionWidth = 200;
  }

  /**
   * Load textures.
   *
   * @param manager the asset manager
   */
  public static void loadTextures(AssetContainer manager) {
    for (int i = 1; i <= 4; i++) {
      manager.load(Assets.playersFolder + "/" + i + ".png", Texture.class, GameType.MINIGAME);
    }
  }

  /**
   * Create textures.
   *
   * @param manager the asset manager
   */
  public static void createTextures(AssetContainer manager) {
    for (int i = 1; i <= 4; i++) {
      manager.load(Assets.playersFolder + "/" + i + ".png", Texture.class, GameType.MINIGAME);
      textures.put(i, manager.get(Assets.playersFolder + "/" + i + ".png", Texture.class));
    }
  }

  /**
   * Get the rocket texture.
   *
   * @return the texture
   */
  public TextureRegion getTexture() {
    return texture;
  }

  /**
   * Load the rocket.
   */
  public void load() {
    texture = new TextureRegion(textures.get(color));
    super.load();
    sprite.setOrigin(sprite.getWidth() / 2, 0);
  }

}
