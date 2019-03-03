package bham.bioshock.client.scenes;

import bham.bioshock.client.Assets;
import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.gameboard.hud.FuelBar;
import bham.bioshock.client.scenes.gameboard.hud.ScoreBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Hud implements Disposable {

  private final Skin skin;
  private final float gameWidth;
  private final float gameHeight;
  public Stage stage;
  public FitViewport viewport;
  private Store store;
  private Router router;

  private ShapeRenderer sr;
  private SpriteBatch batch;

  private FuelBar fuelBar;
  private ScoreBoard scoreBoard;
  
  public Hud(SpriteBatch batch, Skin skin, int gameWidth, int gameHeight, Store store, Router router) {
    this.store = store;
    this.router = router;
    this.skin = skin;
    this.gameWidth = gameWidth;
    this.gameHeight = gameHeight;
    this.batch = batch;

    OrthographicCamera camera = new OrthographicCamera();
    viewport = new FitViewport(this.gameWidth, this.gameHeight, camera);
    stage = new Stage(viewport, batch);

    sr = new ShapeRenderer();

    scoreBoard = new ScoreBoard(stage, batch, skin);
    fuelBar = new FuelBar(stage, batch, skin);
    
    batch.begin();
    batch.end();
  }

  }


    Gdx.gl.glEnable(GL30.GL_BLEND);
    Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    
    sr.begin(ShapeType.Filled);
    sr.setProjectionMatrix(batch.getProjectionMatrix());
    sr.end();
    Gdx.gl.glDisable(GL30.GL_BLEND);

  }

  // private void setupTopBar() {
  //   topBar = new HorizontalGroup();
  //   topBar.setFillParent(true);
  //   topBar.top();

  //   // Adds widgets to the topBar
  //   optionsMenu = new SelectBox(skin);
  //   String[] menuOptions = {"Options Menu", "Settings", "Quit to main menu", "Quit to Desktop"};
  //   optionsMenu.setItems(menuOptions);
  //   optionsMenu.setSelected(menuOptions[0]);
  //   topBar.addActor(optionsMenu);
  //   topBar.setPosition(0, 0);
  //   stage.addActor(topBar);

  //   // Add listeners for each option
  //   optionsMenu.addListener(
  //       new ChangeListener() {
  //         @Override
  //         public void changed(ChangeEvent event, Actor actor) {
  //           int selected = optionsMenu.getSelectedIndex();
  //           switch (selected) {
  //             case 1:
  //               optionsMenu.setSelected(menuOptions[0]);
  //               router.call(Route.PREFERENCES);
  //               break;
  //             case 2:
  //               optionsMenu.setSelected(menuOptions[0]);
  //               router.call(Route.MAIN_MENU);
  //               break;
  //             case 3:
  //               optionsMenu.setSelected(menuOptions[0]);
  //               Gdx.app.exit();
  //               break;
  //           }
  //         }
  //       });
  // }

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

  @Override
  public void dispose() {
    stage.dispose();
  }
}
