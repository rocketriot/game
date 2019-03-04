package bham.bioshock.client.scenes;

import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.gameboard.hud.FuelBar;
import bham.bioshock.client.scenes.gameboard.hud.PauseMenu;
import bham.bioshock.client.scenes.gameboard.hud.ScoreBoard;
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
  private final float gameWidth;
  private final float gameHeight;
  public Stage stage;
  public FitViewport viewport;
  private Store store;

  private FuelBar fuelBar;
  private ScoreBoard scoreBoard;
  private PauseMenu pauseMenu;
  
  public Hud(SpriteBatch batch, Skin skin, int gameWidth, int gameHeight, Store store, Router router) {
    this.store = store;
    this.skin = skin;
    this.gameWidth = gameWidth;
    this.gameHeight = gameHeight;

    OrthographicCamera camera = new OrthographicCamera();
    viewport = new FitViewport(this.gameWidth, this.gameHeight, camera);
    stage = new Stage(viewport, batch);

    pauseMenu = new PauseMenu(stage, batch, skin, router);
    scoreBoard = new ScoreBoard(stage, batch, skin);
    fuelBar = new FuelBar(stage, batch, skin);
  }

  // private void setupBottomBar() {
  //   HorizontalGroup bottomBar = new HorizontalGroup();
  //   bottomBar.bottom();
  //   TextButton endTurnButton = new TextButton("Skip Turn", skin);
  //   bottomBar.addActor(endTurnButton);
  //   stage.addActor(bottomBar);
  //   endTurnButton.addListener(
  //       new ChangeListener() {
  //         @Override
  //         public void changed(ChangeEvent event, Actor actor) {
  //           router.call(Route.SKIP_TURN);
  //         }
  //       });
  // }

  public void updateHud() {
    Player mainPlayer = store.getMainPlayer();

    scoreBoard.render(store.getRound(), store.getPlayers(), store.getMovingPlayer());
    fuelBar.render(mainPlayer.getFuel());
    pauseMenu.render();
    
    // if (store.getMovingPlayer().getId().equals(store.getMainPlayer().getId()) && !turnPromptShown) {
    //   turnPromptShown = true;
    //   showYourTurnDialog();
    // } else if (!store.getMovingPlayer().getId().equals(store.getMainPlayer().getId()) && turnPromptShown) {
    //   turnPromptShown = false;
    // }
  }

  /** Method to ask the user whether they want to start the minigame or not */
  private void showYourTurnDialog() {
    Dialog diag =
        new Dialog("", skin) {
          protected void result(Object object) { }
        };

    diag.text(new Label("Your turn", skin));
    diag.button("Okay", true);
    diag.show(stage);
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
