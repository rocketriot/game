package bham.bioshock.client.scenes.minigame;

import bham.bioshock.client.Router;
import bham.bioshock.client.interfaces.AssetContainer;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;

/**
 * Minigame HUD
 */
public class MinigameHud extends Hud {
  /** Instructions to be displayed */
  private MinigameInstructions minigameInstructions;
  private WeaponContainer weaponContainer;
  private HudClock clock;

  /**
   * Create minigame HUD
   * 
   * @param batch
   * @param skin
   * @param store
   * @param router
   */
  public MinigameHud(SpriteBatch batch, Skin skin, Store store, Router router, AssetContainer assets) {
    super(batch, assets.getSkin(), store, router);

    minigameInstructions = new MinigameInstructions(batch, store, assets);
    weaponContainer = new WeaponContainer(stage, batch, store, router, assets);
    clock = new HudClock(stage, batch, skin, store, router);
  }

  @Override
  public void update() {
    super.update();

    minigameInstructions.render();
    weaponContainer.render();
    clock.render();
  }
}
