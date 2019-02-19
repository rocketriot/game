package bham.bioshock.minigame.models;

import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Bullet extends Entity {

  static TextureRegion texture;

  public Bullet(World w, float x, float y) {
    super(w, x, y);
    fromGround = -10;
  }

  @Override
  public TextureRegion getTexture() {
    return texture;
  }

  public static void loadTextures() {
    texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/minigame/bullet.png")));
  }
}
