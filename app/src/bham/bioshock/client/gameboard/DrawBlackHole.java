package bham.bioshock.client.gameboard;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.models.BlackHole;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class DrawBlackHole extends DrawEntity {
  private Sprite sprite;

  public DrawBlackHole(Batch batch, AssetContainer assets) {
    super(batch, assets);
    sprite = generateSprite(Assets.blackhole);
  }

  public void draw(BlackHole blackHole, int PPS) {
    this.draw(blackHole, PPS, true);
  }

  public void draw(BlackHole blackHole, int PPS, boolean canDrawBlackHole) {
    // Make black hole transparent if it can't be added
    sprite.setAlpha(canDrawBlackHole ? 1f : 0.2f);

    sprite.setX(blackHole.getCoordinates().getX() * PPS);
    sprite.setY(blackHole.getCoordinates().getY() * PPS);
    sprite.draw(batch);
  }

  public void resize(int PPS) {
    sprite.setSize(PPS * BlackHole.WIDTH, PPS * BlackHole.HEIGHT);
  }

  public void dispose() {
    sprite.getTexture().dispose();
  }
}
