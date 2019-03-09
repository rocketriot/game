package bham.bioshock.minigame.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import bham.bioshock.minigame.worlds.World;
import java.util.HashMap;

public class Rocket extends Entity {

  private static final long serialVersionUID = 471939014038953098L;
  
  private static HashMap<Integer, Texture> textures = new HashMap<>();
  private Integer color;
  private TextureRegion texture;

  public Rocket(World w, float x, float y, int color) {
    super(w, x, y, EntityType.ROCKET);
    this.color = color;
    width = 400;
    height = 400;
    collisionHeight = 400;
    collisionWidth = 200;
  }

  public static void loadTextures() {
    textures.put(1, new Texture(Gdx.files.internal("app/assets/entities/players/1.png")));
    textures.put(2, new Texture(Gdx.files.internal("app/assets/entities/players/2.png")));
    textures.put(3, new Texture(Gdx.files.internal("app/assets/entities/players/3.png")));
    textures.put(4, new Texture(Gdx.files.internal("app/assets/entities/players/4.png")));
  }

  public TextureRegion getTexture() {
    return texture;
  }

  public void load() {
    texture = new TextureRegion(textures.get(color));
    super.load();
    sprite.setOrigin(sprite.getWidth() / 2, 0);
  }
  
}
