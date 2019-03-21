package bham.bioshock.client.scenes;

import bham.bioshock.client.Assets;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.Store;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class PauseMenu extends HudElement {
  private ShapeRenderer sr;

  private boolean isPaused;

  private Sprite pauseButton;
  private VerticalGroup menuOptions;
  private TextButton quitLabel;

  PauseMenu(Stage stage, SpriteBatch batch, Skin skin, Store store, Router router) {
    super(stage, batch, skin, store, router);
    
    sr = new ShapeRenderer();
  }

  protected void setup() {
    FileHandle file = Gdx.files.internal(Assets.pauseIcon);

    Texture texture = new Texture(file);
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

    pauseButton = new Sprite(texture);
    pauseButton.setPosition(16, 16);
    pauseButton.setSize(50, 50);

    menuOptions = new VerticalGroup();
    menuOptions.setFillParent(true);
    menuOptions.center();
    stage.addActor(menuOptions);
    
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

  protected void render() {
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

    menuOptions.setVisible(isPaused);

    batch.begin();
    pauseButton.draw(batch);
    batch.end();
  }

  void touchDown(Vector3 mouse) {
    // If pause button clicked, toggle pause menu
    if (pauseButton.getX() <= mouse.x
        && mouse.x <= pauseButton.getX() + pauseButton.getWidth()
        && pauseButton.getY() <= mouse.y
        && mouse.y <= pauseButton.getY() + pauseButton.getHeight()) {
      isPaused = !isPaused;
    }
  }

  boolean isPaused() {
    return isPaused;
  }
}
