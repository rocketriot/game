package bham.bioshock.client.scenes;

import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/** Base HUD element used in the game board and minigame HUDs */
public abstract class HudElement {
  /** Skin to use in the HUD */
  protected final Skin skin;

  /** Used to add elements to the screen */
  protected Stage stage;

  /** Used to draw elements */
  protected SpriteBatch batch;

  /** Contains all the data */
  protected Store store;

  /** Used to call controller methods */
  protected Router router;

  /** Contains all the assets */
  protected AssetContainer assets;

  public HudElement(
      Stage stage, SpriteBatch batch, AssetContainer assets, Store store, Router router) {
    this.stage = stage;
    this.batch = batch;
    this.assets = assets;
    this.skin = assets.getSkin();
    this.store = store;
    this.router = router;

    setup();
  }

  /** Initial HUD element setup to use on first show */
  protected abstract void setup();
}
