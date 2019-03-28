package bham.bioshock.client.gameboard;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.models.Upgrade;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/** Draws an upgrade on the game board */
public class DrawUpgrade extends DrawEntity {
  private Sprite sprite;

  public DrawUpgrade(Batch batch, AssetContainer assets) {
    super(batch, assets);

    sprite = generateSprite(Assets.upgrade);
  }

  /**
   * Draws a upgrade on the game board
   *
   * @param upgrade the upgrade to draw
   * @param PPS the size to draw the upgrade
   */
  public void draw(Upgrade upgrade, int PPS) {
    sprite.setX(upgrade.getCoordinates().getX() * PPS);
    sprite.setY(upgrade.getCoordinates().getY() * PPS);
    sprite.draw(batch);
  }

  /**
   * Resizes the sprites when zooming
   *
   * @param PPS the size to draw the upgrade
   */
  public void resize(int PPS) {
    sprite.setSize(PPS, PPS);
  }

  /** Dispose of the sprite when not needed */
  public void dispose() {
    sprite.getTexture().dispose();
  }
}
