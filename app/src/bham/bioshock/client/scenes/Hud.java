package bham.bioshock.client.scenes;

import bham.bioshock.client.Assets;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class Hud implements Disposable {

  private final Skin skin;
  private final float gameWidth;
  private final float gameHeight;
  public Stage stage;
  public FitViewport viewport;
  private Store store;
  private Router router;

  private Table scoreBoard;
  private Label roundLabel;
  private Image turnPointer;

  public Hud(
      SpriteBatch batch, Skin skin, int gameWidth, int gameHeight, Store store, Router router) {
    this.store = store;
    this.router = router;
    this.skin = skin;
    this.gameWidth = gameWidth / 1.5f;
    this.gameHeight = gameHeight / 1.5f;
    viewport = new FitViewport(this.gameWidth, this.gameHeight, new OrthographicCamera());
    stage = new Stage(viewport, batch);
    
    generateStats();

    turnPointer = new Image(new Texture(Assets.turnPointer));
  }

  private void generateStats() {
    VerticalGroup stats = new VerticalGroup();
    stats.setFillParent(true);
    stats.top();
    stats.left();
    stats.pad(16);
    stage.addActor(stats);

    roundLabel = new Label("Round: 1", skin);
    stats.addActor(roundLabel);
        
    scoreBoard = new Table();
    scoreBoard.padTop(16);
    stats.addActor(scoreBoard);
  }

  // private void setupFuelBar() {
  //   fuelBar = new ProgressBar(0, 100, 1, false, skin);
  //   fuelBar.setValue(100);
  //   fuelString = "Fuel: " + "100.0" + "/100.0";
  //   fuelLabel = new TextArea(fuelString, skin);
  //   topBar.addActor(fuelLabel);
  //   topBar.addActor(fuelBar);
  // }

  // private void setupScoreList() {
  //   table = new Table();
  //   table.top();
  //   table.setPosition(0, 0);
  //   table.setFillParent(true);
  //   stage.addActor(table);

  //   labels = new ArrayList<>();

  //   for (int i = 0; i < 4; i++) {
  //     String pointsString = "Player" + i + ": " + "0";
  //     labels.add(new Label(pointsString, skin));
  //   }

  //   for (Label l : labels) {
  //     table.add(l);
  //     table.padLeft((gameWidth - l.getPrefWidth()));
  //     table.row();
  //   }
  // }

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
    // Update round label
    roundLabel.setText("Round " + store.getRound());
    
    scoreBoard.clearChildren();

    for (Player player : store.getPlayers()) {;
      Player movingPlayer = store.getMovingPlayer();
      boolean isPlayersTurn = player.getId().equals(movingPlayer.getId());

      if (isPlayersTurn) {
        scoreBoard.add(turnPointer)
          .width(30)
          .height(30)
          .padTop(8)
          .padRight(4);
      }
      else {
        scoreBoard.add()
          .width(30)
          .height(30)
          .padTop(8);
      }


      Label usernameLabel = new Label(player.getUsername(), skin);
      scoreBoard.add(usernameLabel)
        .padTop(8)
        .fillX()
        .align(Align.left);

      if (player.isCpu()) {
        Label cpuLabel = new Label("CPU", skin);
        scoreBoard.add(cpuLabel)
          .padTop(8);
      } else {
        scoreBoard.add();
      }
      
      Label pointsLabel = new Label(player.getPoints() + "", skin);
      scoreBoard.add(pointsLabel)
        .padTop(8)
        .padLeft(16)
        .fillX()
        .align(Align.left);
      
      scoreBoard.row();
    }

    //    fuelBar.setValue(store.getMainPlayer().getFuel());
    //    fuelString = "Fuel: " + store.getMainPlayer().getFuel();
    //    fuelLabel.setText(fuelString);


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

  @Override
  public void dispose() {
    stage.dispose();
  }
}
