package bham.bioshock.client.scenes.gameboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public class DrawEntity {
  protected Batch batch;

  public DrawEntity(Batch batch) {
    this.batch = batch;
  }

  /** Generates an array of sprites from a folder */
  public ArrayList<Sprite> generateSprites(String path) {
    ArrayList<Sprite> sprites = new ArrayList<>();

    // Get files
    FileHandle[] fileHandle = Gdx.files.internal(path).list();

    // Generate sprites from a folder
    for (FileHandle file : fileHandle) {
      Sprite sprite = generateSprite(file.path());
      sprites.add(sprite);
    }

    return sprites;
  }

  /** Generates a sprite from a file */
  public Sprite generateSprite(String path) {
    // Get file
    FileHandle file = Gdx.files.internal(path);

    // Generate texture
    Texture texture = new Texture(file);
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

    // Generate sprite
    return new Sprite(texture);
  }
}
