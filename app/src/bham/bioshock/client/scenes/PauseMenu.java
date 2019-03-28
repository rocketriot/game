package bham.bioshock.client.scenes;

import bham.bioshock.Config;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/** Displays a pause menu on the HUD */
public class PauseMenu extends HudElement {
  /** Used to draw a dark overlay on the game */
  private ShapeRenderer sr;

  /** Specifies if the current game is paused */
  private boolean isPaused;

  /** Pause button that when clicked shows the pause menu */
  private Sprite pauseButton;

  /** Lists all the options in the pause menu */
  private VerticalGroup menuOptions;

  /** Button to quit the game */
  private TextButton quitLabel;

  PauseMenu(Stage stage, SpriteBatch batch, AssetContainer assets, Store store, Router router) {
    super(stage, batch, assets, store, router);
    sr = new ShapeRenderer();
  }

  /** Setup the pause menu */
  protected void setup() {
    Texture texture = assets.get(Assets.pauseIcon, Texture.class);
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

    // Setup the pause button
    pauseButton = new Sprite(texture);
    pauseButton.setPosition(16, 16);
    pauseButton.setSize(50, 50);

    // Setup the pause menu options
    menuOptions = new VerticalGroup();
    menuOptions.setFillParent(true);
    menuOptions.center();
    stage.addActor(menuOptions);

    // Add the quit button the the menu
    quitLabel = new TextButton("Quit Game", skin);
    quitLabel.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            router.call(Route.MAIN_MENU);
          }
        });
    menuOptions.addActor(quitLabel);
  }

  /** Render the pause menu */
  protected void render() {
    // Only show the overlay if paused
    if (isPaused) {
      Gdx.gl.glEnable(GL30.GL_BLEND);
      Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

      // Render overlay
      sr.begin(ShapeType.Filled);
      sr.setProjectionMatrix(batch.getProjectionMatrix());
      sr.setColor(new Color(0, 0, 0, 0.5f));
      sr.rect(0, 0, Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT);
      sr.end();

      Gdx.gl.glDisable(GL30.GL_BLEND);
    }

    // Only show the menu if paused
    menuOptions.setVisible(isPaused);

    batch.begin();
    pauseButton.draw(batch);
    if (isPaused) menuOptions.draw(batch, 1);
    batch.end();
  }

  /** Checks if the pause button has been clicked */
  void touchDown(Vector3 mouse) {
    // If pause button clicked, toggle pause menu
    if (pauseButton.getX() <= mouse.x
        && mouse.x <= pauseButton.getX() + pauseButton.getWidth()
        && pauseButton.getY() <= mouse.y
        && mouse.y <= pauseButton.getY() + pauseButton.getHeight()) {
      isPaused = !isPaused;
    }
  }

  /** Returns if the pause menu is open or not */
  boolean isPaused() {
    return isPaused;
  }
}
