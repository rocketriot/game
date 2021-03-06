package bham.bioshock.client.gameboard;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.models.Player;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

/** Draws a planet on the game board */
public class DrawPlanet extends DrawEntity {
  private ArrayList<Sprite> planetSprites = new ArrayList<>();
  private ArrayList<Sprite> flagSprites = new ArrayList<>();

  public DrawPlanet(Batch batch, AssetContainer assets) {
    super(batch, assets);

    for (int i = 1; i <= 4; i++) {
      planetSprites.add(generateSprite(Assets.planetsFolder + "/" + i + ".png"));
    }
    for (int i = 1; i <= 4; i++) {
      flagSprites.add(generateSprite(Assets.flagsFolder + "/" + i + ".png"));
    }
  }

  /**
   * Draws a planet on the game board
   *
   * @param upgrade the planet to draw
   * @param PPS the size to draw the planet
   */
  public void draw(Planet planet, int PPS) {
    Sprite planetSprite = planetSprites.get(planet.getTextureID());

    planetSprite.setX(planet.getCoordinates().getX() * PPS);
    planetSprite.setY(planet.getCoordinates().getY() * PPS);
    planetSprite.draw(batch);

    Player player = planet.getPlayerCaptured();

    // Draw a flag if a planet owns the planet
    if (player != null) {
      Sprite flagSprite = flagSprites.get(player.getTextureID());

      flagSprite.setX(planet.getCoordinates().getX() * PPS);
      flagSprite.setY(planet.getCoordinates().getY() * PPS);
      flagSprite.draw(batch);
    }
  }

  /**
   * Resizes the sprites when zooming
   *
   * @param PPS the size to draw the planet
   */
  public void resize(int PPS) {
    planetSprites.forEach(sprite -> sprite.setSize(PPS * Planet.WIDTH, PPS * Planet.HEIGHT));
    flagSprites.forEach(sprite -> sprite.setSize(PPS * Planet.WIDTH, PPS * Planet.HEIGHT));
  }

  /** Dispose of the sprite when not needed */
  public void dispose() {
    planetSprites.forEach(sprite -> sprite.getTexture().dispose());
    flagSprites.forEach(sprite -> sprite.getTexture().dispose());
  }
}
