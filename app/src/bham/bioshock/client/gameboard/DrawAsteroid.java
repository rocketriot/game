package bham.bioshock.client.gameboard;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.models.Asteroid;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

/** Draws an asteroid on the game board */
public class DrawAsteroid extends DrawEntity {
  private ArrayList<Sprite> sprites = new ArrayList<>();

  public DrawAsteroid(Batch batch, AssetContainer assets) {
    super(batch, assets);

    // Add the asteroid sprites
    for (int i = 1; i <= 5; i++) {
      sprites.add(generateSprite(Assets.asteroidsFolder + "/" + i + ".png"));
    }
  }

  /**
   * Draws an asteroid on the game board
   *
   * @param asteroid the asteroid to draw
   * @param PPS the size to draw the asteroid
   */
  public void draw(Asteroid asteroid, int PPS) {
    Sprite sprite = sprites.get(asteroid.getTextureID());

    sprite.setX(asteroid.getCoordinates().getX() * PPS);
    sprite.setY(asteroid.getCoordinates().getY() * PPS);
    sprite.draw(batch);
  }

  /**
   * Resizes the sprites when zooming
   *
   * @param PPS the size to draw the asteroid
   */
  public void resize(int PPS) {
    sprites.forEach(sprite -> sprite.setSize(PPS * Asteroid.WIDTH, PPS * Asteroid.HEIGHT));
  }

  /** Dispose of the sprite when not needed */
  public void dispose() {
    sprites.forEach(sprite -> sprite.getTexture().dispose());
  }
}
