package bham.bioshock.minigame.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class Rocket extends Entity {

  private static HashMap<Integer, Texture> textures = new HashMap<>();
  private Integer color;
  private TextureRegion texture;

  public Rocket(float _x, float _y, int _color) {
    super(_x, _y);
    color = _color;
    SIZE = 500;
  }

  public static void loadTextures() {
    textures.put(1, new Texture(Gdx.files.internal("app/assets/entities/rockets/1.png")));
    textures.put(2, new Texture(Gdx.files.internal("app/assets/entities/rockets/2.png")));
    textures.put(3, new Texture(Gdx.files.internal("app/assets/entities/rockets/3.png")));
    textures.put(4, new Texture(Gdx.files.internal("app/assets/entities/rockets/4.png")));
  }

  public TextureRegion getTexture() {
    return texture;
  }

  public void load() {
    texture = new TextureRegion(textures.get(color));
    sprite = new Sprite(getTexture());
    sprite.setSize(getSize(), getSize());
    sprite.setOrigin(0, 0);
    sprite.setOrigin(sprite.getWidth() / 2, 0);
  }
}
