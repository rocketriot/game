package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.client.Assets;
import bham.bioshock.common.models.Fuel;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class DrawFuel extends DrawEntity {
  private Sprite sprite;

  public DrawFuel(Batch batch) {
    super(batch);

    sprite = generateSprite(Assets.fuel);
  }

  public void draw(Fuel fuel, int PPS) {
    sprite.setX(fuel.getCoordinates().getX() * PPS);
    sprite.setY(fuel.getCoordinates().getY() * PPS);
    sprite.draw(batch);
  }

  public void resize(int PPS) {
    sprite.setSize(PPS, PPS);
  }

  public void dispose() {
    sprite.getTexture().dispose();
  }
}
