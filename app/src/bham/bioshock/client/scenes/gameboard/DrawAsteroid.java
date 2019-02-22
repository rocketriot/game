package bham.bioshock.client.scenes.gameboard;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import bham.bioshock.client.Assets;
import bham.bioshock.common.models.Asteroid;

public class DrawAsteroid extends DrawEntity {
    ArrayList<Sprite> sprites = new ArrayList<>(); 

    public DrawAsteroid(Batch batch) {
        super(batch);

        sprites = generateSprites(Assets.asteroidsFolder);
    }

    public void draw(Asteroid asteroid, int PPS) {
      Sprite sprite = sprites.get(asteroid.getTextureID());

      sprite.setX(asteroid.getCoordinates().getX() * PPS);
      sprite.setY(asteroid.getCoordinates().getY() * PPS);
      sprite.draw(batch);
    }

    public void resize(int PPS) {
        sprites.forEach(sprite -> sprite.setSize(PPS * 3, PPS * 4));
    }
    
    public void dispose() {
        sprites.forEach(sprite -> sprite.getTexture().dispose());
    }
}
