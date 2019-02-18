package bham.bioshock.client.scenes;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;

public class Hud implements Disposable {

  private Store store;
  private Router router;

  private final Skin skin;
  private final float gameWidth;
  private final float gameHeight;
  public Stage stage;
  public FitViewport viewport;
  private HorizontalGroup topBar;
  private SelectBox optionsMenu;
  private ProgressBar fuelBar;
  private String fuelString;
  private TextArea fuelLabel;
  private Table table;
  private ArrayList<Label> labels;

  public Hud(SpriteBatch batch, Skin skin, int gameWidth, int gameHeight, Store store, Router router) {
    this.store = store;
    this.router = router;
    this.skin = skin;
    this.gameWidth = gameWidth / 1.5f;
    this.gameHeight = gameHeight / 1.5f;
    viewport = new FitViewport(this.gameWidth, this.gameHeight, new OrthographicCamera());
    stage = new Stage(viewport, batch);
    setupTopBar();
    setupFuelBar();
    setupScoreList();
    setupBottomBar();
  }

  private void setupFuelBar() {
    fuelBar = new ProgressBar(0, 100, 1, false, skin);
    fuelBar.setValue(100);
    fuelString = "Fuel: " + "100.0" + "/100.0";
    fuelLabel = new TextArea(fuelString, skin);
    topBar.addActor(fuelLabel);
    topBar.addActor(fuelBar);
  }

  private void setupScoreList() {
    table = new Table();
    table.top();
    table.setPosition(0, 0);
    table.setFillParent(true);
    stage.addActor(table);

    labels = new ArrayList<>();

    for (int i = 0; i < 4; i++) {
      String pointsString = "Player" + i + ": " + "0";
      labels.add(new Label(pointsString, skin));
    }

    for (Label l : labels) {
      table.add(l);
      table.padLeft((gameWidth - l.getPrefWidth()));
      table.row();
    }
  }

  private void setupTopBar() {
    topBar = new HorizontalGroup();
    topBar.setFillParent(true);
    topBar.top();

    // Adds widgets to the topBar
    optionsMenu = new SelectBox(skin);
    String[] menuOptions = {"Options Menu", "Settings", "Quit to main menu", "Quit to Desktop"};
    optionsMenu.setItems(menuOptions);
    optionsMenu.setSelected(menuOptions[0]);
    topBar.addActor(optionsMenu);
    topBar.setPosition(0, 0);
    stage.addActor(topBar);

    // Add listeners for each option
    optionsMenu.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        int selected = optionsMenu.getSelectedIndex();
        switch (selected) {
          case 1:
            optionsMenu.setSelected(menuOptions[0]);
            router.call(Route.PREFERENCES);
            break;
          case 2:
            optionsMenu.setSelected(menuOptions[0]);
            router.call(Route.MAIN_MENU);
            break;
          case 3:
            optionsMenu.setSelected(menuOptions[0]);
            Gdx.app.exit();
            break;
        }
      }
    });
  }

  private void setupBottomBar() {
    HorizontalGroup bottomBar = new HorizontalGroup();
    bottomBar.bottom();
    TextButton endTurnButton = new TextButton("End Turn", skin);
    bottomBar.addActor(endTurnButton);
    stage.addActor(bottomBar);
    endTurnButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        router.call(Route.END_TURN);
      }
    });
  }


  public void updateHud() {
    ArrayList<Player> players = store.getPlayers();

    if (players.size() != store.MAX_PLAYERS) return;

    fuelBar.setValue(store.getMainPlayer().getFuel());
    fuelString = "Fuel: " + store.getMainPlayer().getFuel() + "/100.0";
    fuelLabel.setText(fuelString);

    table.clearChildren();
    labels = new ArrayList<>();

    for (int i = 0; i < players.size(); i++) {
      String pointsString = (players.get(i).getUsername() + ": " + players.get(i).getPoints());
      labels.add(new Label(pointsString, skin));
    }

    for (Label l : labels) {
      table.add(l);
      table.padLeft((gameWidth - l.getPrefWidth()));
      table.row();
    }
  }

  public Stage getStage() {
    return stage;
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
