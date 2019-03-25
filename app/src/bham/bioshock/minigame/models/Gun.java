package bham.bioshock.minigame.models;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import bham.bioshock.minigame.worlds.World;

public class Gun extends Entity {

  private static final long serialVersionUID = 7208353527077911911L;
  
  private static TextureRegion texture;
  
  public Gun(World w, float x, float y) {
    super(w, x, y, EntityType.GUN);
    setRotation(0);
    fromGround = -5;
  }

  @Override
  public TextureRegion getTexture() {
    return texture;
  }

  public static void createTextures(AssetManager manager) {
    texture = new TextureRegion(manager.get("app/assets/minigame/gun.png", Texture.class));
  }
  public static void loadTextures(AssetManager manager) {
    manager.load("app/assets/minigame/gun.png", Texture.class);
  }

}
