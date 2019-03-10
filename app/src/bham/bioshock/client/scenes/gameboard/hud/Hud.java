package bham.bioshock.client.scenes.gameboard.hud;

import bham.bioshock.client.Router;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Hud implements Disposable {
  private final Skin skin;
  public Stage stage;
  public FitViewport viewport;
  private Store store;

  private FuelBar fuelBar;
  private ScoreBoard scoreBoard;
  private PauseMenu pauseMenu;
  private TurnStartText turnStartText;
  private SkipTurnButton skipTurnButton;

  public Hud(SpriteBatch batch, Skin skin, Store store, Router router) {
    this.store = store;
    this.skin = skin;

    OrthographicCamera camera = new OrthographicCamera();
    viewport = new FitViewport(Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT, camera);
    stage = new Stage(viewport, batch);

    turnStartText = new TurnStartText(batch, store);
    pauseMenu = new PauseMenu(stage, batch, skin, store, router);
    scoreBoard = new ScoreBoard(stage, batch, skin, store, router);
    fuelBar = new FuelBar(stage, batch, skin, store, router);
    skipTurnButton = new SkipTurnButton(stage, batch, skin, store, router);
  }

  public void updateHud() {
    Player mainPlayer = store.getMainPlayer();

    scoreBoard.render(store.getRound(), store.getPlayers(), store.getMovingPlayer());
    fuelBar.render(mainPlayer.getFuel());
    pauseMenu.render();
    turnStartText.render();
    skipTurnButton.render();
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
  

  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    Vector3 mouse = getMouseCoordinates(screenX, screenY);
    pauseMenu.touchDown(mouse);

    return false;
  }

  public boolean isPaused() {
    return pauseMenu.isPaused();
  }
}
