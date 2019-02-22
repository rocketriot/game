package bham.bioshock.client.scenes.gameboard;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import bham.bioshock.client.Assets;
import bham.bioshock.common.models.Planet;

public class DrawPlanet extends DrawEntity {
    ArrayList<Sprite> sprites = new ArrayList<>(); 

    public DrawPlanet(Batch batch) {
        super(batch);

        sprites = generateSprites(Assets.planetsFolder);
    }

    public void draw(Planet planet, int PPS) {
      Sprite sprite = sprites.get(planet.getTextureID());

      sprite.setX(planet.getCoordinates().getX() * PPS);
      sprite.setY(planet.getCoordinates().getY() * PPS);
      sprite.draw(batch);
    }

    public void resize(int PPS) {
        sprites.forEach(sprite -> sprite.setSize(PPS * 3, PPS * 3));
    }
    
    public void dispose() {
        sprites.forEach(sprite -> sprite.getTexture().dispose());
    }
}
