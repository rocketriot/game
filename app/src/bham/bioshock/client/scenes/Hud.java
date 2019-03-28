package bham.bioshock.client.scenes;

import bham.bioshock.Config;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** Base HUD logic for the game board and minigame HUD */
public class Hud implements Disposable, InputProcessor {
  /** Skin to use in the HUD */
  protected final Skin skin;

  /** Used to add elements to the screen */
  protected Stage stage;

  /** Specifies how to display elements */
  protected FitViewport viewport;

  /** Stores all the data */
  protected Store store;

  /** Stores all the assets */
  protected AssetContainer assets;

  /** Base pause menu used in all HUDs */
  private PauseMenu pauseMenu;

  public Hud(SpriteBatch batch, AssetContainer assets, Store store, Router router) {
    this.store = store;
    this.skin = assets.getSkin();

    OrthographicCamera camera = new OrthographicCamera();
    viewport = new FitViewport(Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT, camera);
    stage = new Stage(viewport, batch);

    pauseMenu = new PauseMenu(stage, batch, assets, store, router);
  }

  /** Updates all the elements of the HUD */
  public void update() {
    pauseMenu.render();
  }

  /** Returns the stage */
  public Stage getStage() {
    return stage;
  }

  /** Draws the stage */
  public void draw() {
    stage.draw();
  }

  /**
   * Gets the coordinates of where the mouse is located on the screen
   *
   * @param screenX the x position of the mouse on the screen
   * @param screenY the y position of the mouse on the screen
   * @return a vector of the mouse coordinates
   */
  private Vector3 getMouseCoordinates(int screenX, int screenY) {
    Vector3 vector = new Vector3(screenX, screenY, 0);
    Vector3 coordinates = viewport.unproject(vector);

    return coordinates;
  }

  @Override
  public void dispose() {
    stage.dispose();
  }

  /** Check if the game is paused or not */
  public boolean isPaused() {
    return pauseMenu.isPaused();
  }

  /** Returns the viewport */
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

  /** Handles click events */
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
