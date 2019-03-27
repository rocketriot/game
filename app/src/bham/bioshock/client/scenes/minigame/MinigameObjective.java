package bham.bioshock.client.scenes.minigame;

import bham.bioshock.Config;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.scenes.HudElement;
import bham.bioshock.common.models.store.Store;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MinigameObjective extends HudElement {
  private BitmapFont font;

  MinigameObjective(Stage stage, SpriteBatch batch, Store store, Router router, AssetContainer assets) {
    super(stage, batch, assets, store, router);
  }

  protected void setup() {
    font = assets.getFont(48);
  }

  public void render() {
    String name = store.getMinigameStore().getObjective().name();
    
    batch.begin();
    font.draw(batch, name, 50, Config.GAME_WORLD_HEIGHT - 62);
    batch.end();
  }
}
