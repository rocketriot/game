package bham.bioshock.minigame.models;

import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Heart extends Entity {

  private static final long serialVersionUID = -7192308795772982285L;

  private static TextureRegion texture;

  public Heart(World w, float x, float y) {
    super(w, x, y, EntityType.HEART);
    setRotation(0);
    fromGround = -5;
    collisionHeight = getHeight() / 2;
  }

  @Override
  public TextureRegion getTexture() {
    return texture;
  }

  public static void createTextures(AssetManager manager) {
    Texture wholeImage = new Texture("app/assets/minigame/hearts.png");
    texture = new TextureRegion(wholeImage, 0, 0, 50, 50);
  }

  public static void loadTextures(AssetManager manager) {
    manager.load("app/assets/minigame/hearts.png", Texture.class);
  }


  /**
   * Collisions
   **/
  @Override
  public boolean canCollideWith(Entity e) {
    switch (e.type) {
      case ASTRONAUT:
        return true;
      default:
        return false;
    }
  }

  @Override
  public void handleCollision(Entity e) {
    switch (e.type) {
      case ASTRONAUT:
        e.handleCollision(this);
        break;
      default:
        break;
    }
  }

}
