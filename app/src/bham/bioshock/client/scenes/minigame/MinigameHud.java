package bham.bioshock.client.scenes.minigame;

import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Minigame HUD */
public class MinigameHud extends Hud {
  /** Instructions to be displayed */
  private MinigameScoreBoard minigameScoreBoard;

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
  public MinigameHud(SpriteBatch batch, AssetContainer assets, Store store, Router router) {
    super(batch, assets, store, router);

    minigameScoreBoard = new MinigameScoreBoard(stage, batch, store, router, assets);
    minigameInstructions = new MinigameInstructions(batch, store, assets);
    weaponContainer = new WeaponContainer(stage, batch, store, router, assets);
    clock = new HudClock(stage, batch, assets, store, router);
  }

  @Override
  public void update() {
    super.update();

    minigameScoreBoard.render();
    minigameInstructions.render();
    weaponContainer.render();
    clock.render();
  }
}
