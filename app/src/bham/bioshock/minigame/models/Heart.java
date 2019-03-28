package bham.bioshock.minigame.models;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.assets.Assets.GamePart;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** Heart entity can be captured by the player to increase health */
public class Heart extends Entity {

  private static final long serialVersionUID = -7192308795772982285L;

  private static TextureRegion texture;

  /**
   * Creates heart object at specified initial position
   *
   * @param w world
   * @param x position
   * @param y position
   */
  public Heart(World w, float x, float y) {
    super(w, x, y, EntityType.HEART);
    setRotation(0);
    fromGround = 5;
    collisionHeight = getHeight() / 2;
    width = 30;
    height = 30;
  }

  /** Creates texture for rendering */
  public static void createTextures(AssetContainer manager) {
    Texture wholeImage = manager.get(Assets.hearts, Texture.class);
    texture = new TextureRegion(wholeImage, 0, 50, 50, 50);
  }

  /** Queue textures for loading */
  public static void loadTextures(AssetContainer manager) {
    manager.load(Assets.hearts, Texture.class, GamePart.MINIGAME);
  }

  public static Heart getRandom(World world) {
    Position p = world.getRandomPosition();
    return new Heart(world, p.x, p.y);
  }

  /** Returns texture for rendering */
  @Override
  public TextureRegion getTexture() {
    return texture;
  }
}
