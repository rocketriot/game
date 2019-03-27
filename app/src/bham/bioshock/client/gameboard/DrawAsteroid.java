package bham.bioshock.client.gameboard;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.models.Asteroid;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public class DrawAsteroid extends DrawEntity {
  private ArrayList<Sprite> sprites = new ArrayList<>();

  public DrawAsteroid(Batch batch, AssetContainer assets) {
    super(batch, assets);
    
    for(int i=1; i<=5; i++) {
      sprites.add(generateSprite(Assets.asteroidsFolder + "/" + i + ".png"));     
    }
  }

  public void draw(Asteroid asteroid, int PPS) {
    Sprite sprite = sprites.get(asteroid.getTextureID());

    sprite.setX(asteroid.getCoordinates().getX() * PPS);
    sprite.setY(asteroid.getCoordinates().getY() * PPS);
    sprite.draw(batch);
  }

  public void resize(int PPS) {
    sprites.forEach(sprite -> sprite.setSize(PPS * Asteroid.WIDTH, PPS * Asteroid.HEIGHT));
  }

  public void dispose() {
    sprites.forEach(sprite -> sprite.getTexture().dispose());
  }
}
