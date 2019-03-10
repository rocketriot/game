package bham.bioshock.client.scenes.gameboard.hud;

import bham.bioshock.client.Router;
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

  public HudElement(Stage stage, SpriteBatch batch, Skin skin, Store store, Router router) {
    this.stage = stage;
    this.batch = batch;
    this.skin = skin;
    this.store = store;
    this.router = router;

    setup();
  }

  protected abstract void setup();
}
