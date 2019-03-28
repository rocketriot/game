package bham.bioshock.client.scenes.minigame;

import bham.bioshock.Config;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.scenes.HudElement;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.models.Astronaut;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** Displays if the player is holding a weapon or not */
public class WeaponContainer extends HudElement {
  /** Size of the weapon container */
  private final int SIZE = 100;
  /** Radius of the corner of the weapon container */
  private final int RADIUS = 50;
  /** Used to draw the weapon container */
  private ShapeRenderer sr;
  /** Used to display the weapon label */
  private BitmapFont font;
  /** Displays a weapon if the player has one */
  private Image weapon;

  WeaponContainer(
      Stage stage, SpriteBatch batch, Store store, Router router, AssetContainer assets) {
    super(stage, batch, assets, store, router);
    sr = new ShapeRenderer();
  }

  /** Setup the weapon container */
  protected void setup() {
    font = assets.getFont(24);

    weapon = new Image(assets.get(Assets.gun, Texture.class));
    weapon.setSize(100, 100);
    stage.addActor(weapon);
  }

  /** Render the weapon container */
  public void render() {
    // Renders the background of the weapon container
    renderBackground();

    batch.begin();
    font.draw(batch, "WEAPON", Config.GAME_WORLD_WIDTH - 128, Config.GAME_WORLD_HEIGHT - 25);
    batch.end();

    weapon.setPosition(Config.GAME_WORLD_WIDTH - 123, Config.GAME_WORLD_HEIGHT - 140);

    // Only show weapon when the player has a weapon
    Astronaut mainPlayer = store.getMinigameStore().getMainPlayer();
    weapon.setVisible(mainPlayer.getEquipment().haveGun);
  }

  /** Draws the background for the weapon container */
  private void renderBackground() {
    Gdx.gl.glEnable(GL30.GL_BLEND);
    Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

    sr.begin(ShapeType.Filled);
    sr.setProjectionMatrix(batch.getProjectionMatrix());

    // Container background
    sr.setColor(new Color(1, 1, 1, 0.2f));
    sr.rect(Config.GAME_WORLD_WIDTH - SIZE, Config.GAME_WORLD_HEIGHT - SIZE, SIZE, SIZE);
    sr.rect(Config.GAME_WORLD_WIDTH - SIZE - RADIUS, Config.GAME_WORLD_HEIGHT - SIZE, RADIUS, SIZE);
    sr.rect(Config.GAME_WORLD_WIDTH - SIZE, Config.GAME_WORLD_HEIGHT - SIZE - RADIUS, SIZE, RADIUS);
    sr.arc(Config.GAME_WORLD_WIDTH - SIZE, Config.GAME_WORLD_HEIGHT - SIZE, RADIUS, 180f, 90f);

    sr.end();

    Gdx.gl.glDisable(GL30.GL_BLEND);
  }
}
