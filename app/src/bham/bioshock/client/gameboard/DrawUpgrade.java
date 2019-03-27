package bham.bioshock.client.gameboard;

import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.models.Upgrade;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class DrawUpgrade extends DrawEntity {
  private Sprite sprite;

  public DrawUpgrade(Batch batch) {
    super(batch);

    sprite = generateSprite(Assets.upgrade);
  }

  public void draw(Upgrade upgrade, int PPS) {
    sprite.setX(upgrade.getCoordinates().getX() * PPS);
    sprite.setY(upgrade.getCoordinates().getY() * PPS);
    sprite.draw(batch);
  }

  public void resize(int PPS) {
    sprite.setSize(PPS, PPS);
  }

  public void dispose() {
    sprite.getTexture().dispose();
  }
}
