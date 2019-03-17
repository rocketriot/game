package bham.bioshock.minigame.models;

import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Flag extends Entity {

  private static final long serialVersionUID = -374379982329919955L;
  
  public static TextureRegion texture;

  public Flag(World w, float x, float y) {
    super(w, x, y, true, EntityType.FLAG);
    setRotation(0);
  }

  @Override
  public TextureRegion getTexture() {
    return texture;
  }

  public static void loadTextures() {
    texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/minigame/flag.png")));
  }
}
