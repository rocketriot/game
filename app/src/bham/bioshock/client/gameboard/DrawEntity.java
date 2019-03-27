package bham.bioshock.client.gameboard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import bham.bioshock.client.assets.AssetContainer;

public class DrawEntity {
  protected Batch batch;
  protected AssetContainer assets;
  
  public DrawEntity(Batch batch, AssetContainer assets) {
    this.batch = batch;
    this.assets = assets;
  }

  /** Generates a sprite from a file */
  public Sprite generateSprite(String path) {
    Texture texture = assets.get(path, Texture.class);
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

    // Generate sprite
    return new Sprite(texture);
  }
}
