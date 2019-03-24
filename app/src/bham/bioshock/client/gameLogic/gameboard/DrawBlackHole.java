package bham.bioshock.client.gameLogic.gameboard;

import bham.bioshock.client.Assets;
import bham.bioshock.common.models.BlackHole;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public class DrawBlackHole extends DrawEntity {
  private ArrayList<Sprite> sprites = new ArrayList<>();

  public DrawBlackHole(Batch batch) {
    super(batch);

    sprites = generateSprites(Assets.blackholesFolder);
  }

  public void draw(BlackHole blackHole, int PPS) {
    Sprite sprite = sprites.get(blackHole.getTextureID());

    sprite.setX(blackHole.getCoordinates().getX() * PPS);
    sprite.setY(blackHole.getCoordinates().getY() * PPS);
    sprite.draw(batch);
  }

  public void resize(int PPS) {
    sprites.forEach(sprite -> sprite.setSize(PPS * BlackHole.WIDTH, PPS * BlackHole.HEIGHT));
  }

  public void dispose() {
    sprites.forEach(sprite -> sprite.getTexture().dispose());
  }
}
