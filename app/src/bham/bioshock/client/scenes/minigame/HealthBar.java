package bham.bioshock.client.scenes.minigame;

import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.HudElement;
import bham.bioshock.common.models.store.Store;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class HealthBar extends HudElement {
  private ShapeRenderer sr;
  private FontGenerator fontGenerator;

  HealthBar(Stage stage, SpriteBatch batch, Skin skin, Store store, Router router) {
    super(stage, batch, skin, store, router);
    
    sr = new ShapeRenderer();
  }

  protected void setup() {
    fontGenerator = new FontGenerator();
  }

  public void render() {
  }
}
