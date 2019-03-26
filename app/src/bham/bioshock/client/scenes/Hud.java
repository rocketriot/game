package bham.bioshock.client.scenes;
import bham.bioshock.client.Route;
import bham.bioshock.Config;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Store;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Hud implements Disposable, InputProcessor {
  protected final Skin skin;
  protected Stage stage;
  protected FitViewport viewport;
  protected Store store;

  private PauseMenu pauseMenu;

  public Hud(SpriteBatch batch, Skin skin, Store store, Router router) {
    this.store = store;
    this.skin = skin;

    OrthographicCamera camera = new OrthographicCamera();
    viewport = new FitViewport(Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT, camera);
    stage = new Stage(viewport, batch);

    pauseMenu = new PauseMenu(stage, batch, skin, store, router);
  }

  public void update() {
    pauseMenu.render();
  }

  public Stage getStage() {
    return stage;
  }

  public void draw() {
    stage.draw();
  }

  private Vector3 getMouseCoordinates(int screenX, int screenY) {
    Vector3 vector = new Vector3(screenX, screenY, 0);
    Vector3 coordinates = viewport.unproject(vector);

    return coordinates;
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
  
  public boolean isPaused() {
    return pauseMenu.isPaused();
  }

  public FitViewport getViewport() {
    return viewport;
  }

  @Override
  public boolean keyDown(int keycode) {
      return false;
  }

  @Override
  public boolean keyUp(int keycode) {
      return false;
  }

  @Override
  public boolean keyTyped(char character) {
      return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
      return false;
  }
  
  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    Vector3 mouse = getMouseCoordinates(screenX, screenY);
    pauseMenu.touchDown(mouse);

    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
      return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
      return false;
  }

  @Override
  public boolean scrolled(int amount) {
      return false;
  }
}
