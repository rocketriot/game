package bham.bioshock.client.scenes;

import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.common.models.store.Store;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class HudElement {
  protected Stage stage;
  protected SpriteBatch batch;
  protected final Skin skin;
  protected Store store;
  protected Router router;
  protected AssetContainer assets;

  public HudElement(Stage stage, SpriteBatch batch, AssetContainer assets, Store store, Router router) {
    this.stage = stage;
    this.batch = batch;
    this.assets = assets;
    this.skin = assets.getSkin();
    this.store = store;
    this.router = router;

    setup();
  }

  protected abstract void setup();
}
