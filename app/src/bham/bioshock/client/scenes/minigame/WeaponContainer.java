package bham.bioshock.client.scenes.minigame;

import bham.bioshock.client.Assets;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.HudElement;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.models.Astronaut;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class WeaponContainer extends HudElement {
  private ShapeRenderer sr;
  private FontGenerator fontGenerator;

  private Image weapon;

  WeaponContainer(Stage stage, SpriteBatch batch, Skin skin, Store store, Router router) {
    super(stage, batch, skin, store, router);
    
    sr = new ShapeRenderer();
  }

  protected void setup() {
    fontGenerator = new FontGenerator();

    weapon = new Image(new Texture(Gdx.files.internal(Assets.gun)));
  }

  public void render() {
    Astronaut mainPlayer = store.getMinigameStore().getMainPlayer();

    // Only show weapon when the player has a weapon
    weapon.setVisible(mainPlayer.haveGun());
  }
}
