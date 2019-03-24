package bham.bioshock.minigame.models;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
  public static void loadTextures(AssetManager manager) {
    manager.load("app/assets/entities/players/1.png", Texture.class);
    manager.load("app/assets/entities/players/2.png", Texture.class);
    manager.load("app/assets/entities/players/3.png", Texture.class);
    manager.load("app/assets/entities/players/4.png", Texture.class);
  }

  /**
   * Create textures.
   *
   * @param manager the asset manager
   */
  public static void createTextures(AssetManager manager) {
    textures.put(1, manager.get("app/assets/entities/players/1.png", Texture.class));
    textures.put(2, manager.get("app/assets/entities/players/2.png", Texture.class));
    textures.put(3, manager.get("app/assets/entities/players/3.png", Texture.class));
    textures.put(4, manager.get("app/assets/entities/players/4.png", Texture.class));
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
