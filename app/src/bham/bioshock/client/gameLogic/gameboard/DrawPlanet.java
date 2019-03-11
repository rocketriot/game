package bham.bioshock.client.gameLogic.gameboard;

import bham.bioshock.client.Assets;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.models.Player;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public class DrawPlanet extends DrawEntity {
  private ArrayList<Sprite> planetSprites = new ArrayList<>();
  private ArrayList<Sprite> flagSprites = new ArrayList<>();

  public DrawPlanet(Batch batch) {
    super(batch);

    planetSprites = generateSprites(Assets.planetsFolder);
    flagSprites = generateSprites(Assets.flagsFolder);
  }

  public void draw(Planet planet, int PPS) {
    Sprite planetSprite = planetSprites.get(planet.getTextureID());

    planetSprite.setX(planet.getCoordinates().getX() * PPS);
    planetSprite.setY(planet.getCoordinates().getY() * PPS);
    planetSprite.draw(batch);

    Player player = planet.getPlayerCaptured();

    // Draw a flag if a planet owns the planet
    if (player != null) {
      Sprite flagSprite = flagSprites.get(1);

      flagSprite.setX(planet.getCoordinates().getX() * PPS);
      flagSprite.setY(planet.getCoordinates().getY() * PPS);
      flagSprite.draw(batch);
    }
  }

  public void resize(int PPS) {
    planetSprites.forEach(sprite -> sprite.setSize(PPS * 3, PPS * 3));
    flagSprites.forEach(sprite -> sprite.setSize(PPS * 3, PPS * 3));
  }

  public void dispose() {
    planetSprites.forEach(sprite -> sprite.getTexture().dispose());
    flagSprites.forEach(sprite -> sprite.getTexture().dispose());
  }
}