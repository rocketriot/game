package bham.bioshock.client.gameboard;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.models.BlackHole;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/** Draws a black hole on the game board */
public class DrawBlackHole extends DrawEntity {
  private Sprite sprite;

  public DrawBlackHole(Batch batch, AssetContainer assets) {
    super(batch, assets);
    sprite = generateSprite(Assets.blackhole);
  }

  /**
   * Draws a black hole on the game board, by default is able to draw a black hole
   *
   * @param blackHole the black hole to draw
   * @param PPS the size to draw the black hole
   */
  public void draw(BlackHole blackHole, int PPS) {
    this.draw(blackHole, PPS, true);
  }

  /**
   * Draws a black hole on the game board
   *
   * @param blackHole the black hole to draw
   * @param PPS the size to draw the black hole
   * @param canDrawBlackHole specifies if the player has space to add the black hole to the board
   */
  public void draw(BlackHole blackHole, int PPS, boolean canDrawBlackHole) {
    // Make black hole transparent if it can't be added
    sprite.setAlpha(canDrawBlackHole ? 1f : 0.2f);

    sprite.setX(blackHole.getCoordinates().getX() * PPS);
    sprite.setY(blackHole.getCoordinates().getY() * PPS);
    sprite.draw(batch);
  }

  /**
   * Resizes the sprites when zooming
   *
   * @param PPS the size to draw the black hole
   */
  public void resize(int PPS) {
    sprite.setSize(PPS * BlackHole.WIDTH, PPS * BlackHole.HEIGHT);
  }

  /** Dispose of the sprite when not needed */
  public void dispose() {
    sprite.getTexture().dispose();
  }
}
