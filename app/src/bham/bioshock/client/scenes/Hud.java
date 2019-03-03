package bham.bioshock.client.scenes;

import bham.bioshock.client.Assets;
import bham.bioshock.client.Router;
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
  private Table scoreBoard;
  private Label roundLabel;
  private Image turnPointer;
  private SpriteBatch batch;

  float fuelWidth = 48f;
  float fuelPadding = 50f;
  float fuelBorderSize = 12f;
  float fuelMaxHeight;
  float fuelXCoordinate;
  
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

    turnPointer = new Image(new Texture(Assets.turnPointer));
    
    fuelMaxHeight = gameHeight - (fuelPadding * 2) - 50f;
    fuelXCoordinate = gameWidth - (fuelWidth + fuelPadding);

    batch.begin();
    generateStats();
    generateFuelBar(100f);
    batch.end();
  }

  private void generateStats() {
    VerticalGroup stats = new VerticalGroup();
    stats.setFillParent(true);
    stats.top();
    stats.left();
    stats.pad(16);
    stage.addActor(stats);

    roundLabel = new Label("Round " + store.getRound(), skin);
    stats.addActor(roundLabel);
        
    scoreBoard = new Table();
    scoreBoard.padTop(16);
    stats.addActor(scoreBoard);
  }

  private void updateStats() {
    roundLabel.setText("Round " + store.getRound());

    scoreBoard.clearChildren();

    // Add players to scoreboard
    for (Player player : store.getPlayers()) {
      Player movingPlayer = store.getMovingPlayer();
      boolean isPlayersTurn = player.getId().equals(movingPlayer.getId());
      
      // Add the turn pointer to the player whos turn it is
      scoreBoard.add(isPlayersTurn ? turnPointer: null)
        .width(30)
        .height(30)
        .padTop(8)
        .padRight(4);

      // Add name of the user
      Label usernameLabel = new Label(player.getUsername(), skin);
      scoreBoard.add(usernameLabel)
      .padTop(8)
      .fillX()
      .align(Align.left);
      
      // Specify if the player is a CPU
      Label cpuLabel = new Label("CPU", skin);
      scoreBoard.add(player.isCpu() ? cpuLabel : null).padTop(8);
      
      // Add the player's points
      Label pointsLabel = new Label(player.getPoints() + "", skin);
      scoreBoard.add(pointsLabel)
        .padTop(8)
        .padLeft(16)
        .fillX()
        .align(Align.left);
      
      scoreBoard.row();
    }
  }

  private void generateFuelBar(float fuelValue) {
    float height = (fuelValue / Player.MAX_FUEL) * fuelMaxHeight;

    Gdx.gl.glEnable(GL30.GL_BLEND);
    Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    
    sr.begin(ShapeType.Filled);
    sr.setProjectionMatrix(batch.getProjectionMatrix());

    // Fuel bar border
    sr.setColor(new Color(1, 1, 1, 0.2f));
    sr.rect(
      fuelXCoordinate - fuelBorderSize,
      fuelPadding - fuelBorderSize + 50f,
      fuelWidth + (fuelBorderSize * 2),
      fuelMaxHeight + (fuelBorderSize * 2)
      );
    sr.end();
    Gdx.gl.glDisable(GL30.GL_BLEND);
    
    sr.begin(ShapeType.Filled);
    sr.setProjectionMatrix(batch.getProjectionMatrix());

    // 75% to 100%
    sr.setColor(new Color(0xFF433EFF));
    sr.rect(fuelXCoordinate, fuelPadding + 50f, fuelWidth, height);
    
    // 50% to 75%
    sr.setColor(new Color(0xFF8343FF));
    float height2 = Float.min(fuelMaxHeight * 0.75f, height);
    sr.rect(fuelXCoordinate, fuelPadding + 50f, fuelWidth, height2);
    
     // 25% to 50%
    sr.setColor(new Color(0xFFA947FF));
    float height3 = Float.min(fuelMaxHeight * 0.50f, height);
    sr.rect(fuelXCoordinate, fuelPadding + 50f, fuelWidth, height3);
    
    // 0% to 25%
    sr.setColor(new Color(0xFFE04AFF));
    float height4 = Float.min(fuelMaxHeight * 0.25f, height);
    sr.rect(fuelXCoordinate, fuelPadding + 50f, fuelWidth, height4);

    sr.end();
  
    VerticalGroup fuelInfo = new VerticalGroup();
    fuelInfo.setFillParent(true);
    fuelInfo.bottom();
    fuelInfo.right();
    fuelInfo.padRight(50);
    fuelInfo.padBottom(15);
    stage.addActor(fuelInfo);

    Label fuelLabel = new Label(String.format("%.0f", fuelValue), skin);
    fuelLabel.setFontScale(1.2f);
    fuelLabel.setWidth(60);
    fuelLabel.setAlignment(Align.center);
    fuelInfo.addActor(fuelLabel);
    
    Label fuelValueLabel = new Label("FUEL", skin);
    fuelValueLabel.setFontScale(1.2f);
    fuelValueLabel.setWidth(60);
    fuelValueLabel.setAlignment(Align.center);
    fuelInfo.addActor(fuelValueLabel);
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
    batch.begin();
    Player mainPlayer = store.getMainPlayer();

    updateStats();
    generateFuelBar(mainPlayer.getFuel());
    

    // if (store.getMovingPlayer().getId().equals(store.getMainPlayer().getId()) && !turnPromptShown) {
    //   turnPromptShown = true;
    //   showYourTurnDialog();
    // } else if (!store.getMovingPlayer().getId().equals(store.getMainPlayer().getId()) && turnPromptShown) {
    //   turnPromptShown = false;
    // }
    batch.end();
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
