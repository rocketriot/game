package bham.bioshock.minigame.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import bham.bioshock.minigame.worlds.World;

public class Gun extends Entity {

  private static TextureRegion texture;
  
  public Gun(World w, float x, float y) {
    super(w, x, y);
    setRotation(0);
    fromGround = -5;
  }

  @Override
  public TextureRegion getTexture() {
    return texture;
  }

  @Override
  public Player getShooter() {
    return null;
  }

  public static void loadTextures() {
    texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/minigame/gun.png")));
  }

}
