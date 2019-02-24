package bham.bioshock.client.screens;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class JoinScreen extends ScreenMaster {
  
  private Store store;
  
  private final int CELL_PADDING = 100;
  private final int IMAGE_WIDTH = 20;
  private int currentFrame = 0;

  int cols = 26;
  int rows = 1;

  /*animation objects */
  static Animation<TextureRegion> loadingAnimation;
  static Texture loadingSheet;
  static TextureRegion[] loadRegion;
  //SpriteBatch spriteBatch;
  float stateTime = 0f;


  public enum WaitText {
    WAITING("Waiting..."), CONNECTED("Connected");
    
    final String text;
    
    WaitText(String text) {
      this.text = text;
    }
  }

  //players
  private PlayerContainer[] containers;
  private Table table;
  
  public JoinScreen(Router router, Store store) {
    super(router);
    this.store = store;
    stage = new Stage(new ScreenViewport());
    containers = new PlayerContainer[4];
    batch = new SpriteBatch();
  }

  @Override
  public void show() {

    super.show();

    setUpLoadingAnimation();

    buildJoinScreen();
  }

  private void buildJoinScreen() {
    table = new Table();
    table.setFillParent(true);
    //table.setHeight(screen_height * 0.7f);
    //table.setWidth(screen_width * 0.7f);
    table.pad(CELL_PADDING);
    stage.addActor(table);
    
    addStartGameButton();
    loadPlayers();
  }
  
  private void loadPlayers() {
    for(int i=0; i<4; i++) {
      loadingSheet = new Texture(Gdx.files.internal("app/assets/animations/loading_spritesheet.png"));
      loadingSheet.setFilter(TextureFilter.Linear, TextureFilter.Linear);
      containers[i] = new PlayerContainer(loadingSheet, "Player"+(i+1), WaitText.WAITING);
    }

    table.add(containers[0]);
    table.add(containers[1]);
    table.row();
    table.add(containers[2]);
    table.add(containers[3]);

  }

  public void changeWaitLabel(Label label, WaitText text) {
    label.setText(text.text);
  }

  public void changePlayerName(Label label, String player) {
    label.setText(player);
  }
  
  public void addStartGameButton() {
    TextButton startButton = new TextButton("Start Game", skin);
    TextButton miniGameButton = new TextButton("TEST Mini Game", skin);
    startButton.setPosition(screen_width - 150, 40);
    miniGameButton.setPosition(screen_width - 150, 0);
    stage.addActor(startButton);
    stage.addActor(miniGameButton);
    
    startButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        router.call(Route.STOP_MAIN_MUSIC);
        router.call(Route.START_GAME);
      }
    });
    
    miniGameButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        router.call(Route.SEND_MINIGAME_START);
      }
    });
  }
  
  @Override
  protected void setPrevious() {
    backButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        router.call(Route.DISCONNECT_PLAYER);
        store.removeAllPlayers();
        router.back();
      }
    });
  }

  @Override
  public void render(float delta) {
    stateTime += delta;

    super.render(delta);

    /*draw an animation */
    //drawAnimation(20,20,loadingAnimation, stateTime);


    ArrayList<Player> players = store.getPlayers();


    /* update animation */
    for (int i = 0; i < store.MAX_PLAYERS; i++) {
      containers[i].updateAnimation(stateTime);
    }
    
    for(int i=0; i<4; i++) {
      if(players.size() > i) {
        Player p = players.get(i);        
        containers[i].waitingLabel.setText(WaitText.CONNECTED.text);
        containers[i].nameLabel.setText(p.getUsername());

      } else {
        containers[i].waitingLabel.setText(WaitText.WAITING.text);
        containers[i].nameLabel.setText("Player" + (i+1));
      }
    }



  }

  private void drawAnimation(int x, int y, Animation<TextureRegion> animation, float time) {
    batch.begin();
    TextureRegion currentFrame = animation.getKeyFrame(time, true);
    batch.draw(currentFrame, 0,0);
    batch.end();
  }


  private class PlayerContainer extends Container<Table> {

    String waitText;
    Label waitingLabel;
    Label nameLabel;
    LoadingAnimation loadAnim;

    PlayerContainer(Texture img, String name, WaitText text) {
      this.waitText = text.text;

      /*Add loading animation */

      loadAnim = new LoadingAnimation(img);
      //Image image = new Image(img);

      loadAnim.setScaling(Scaling.fit);

      Table t = new Table();
      t.setFillParent(true);
      t.pad(15);

      nameLabel = new Label(name, skin);
      waitingLabel = new Label(waitText, skin);

      t.add(nameLabel).pad(0,0,10,0);
      t.row();
      t.add(loadAnim).expand();
      t.row();
      t.add(waitingLabel).pad(10,0,0,0);
      this.setActor(t);


    }
    public void updateAnimation(float stateTime) {
      loadAnim.updateAnimation(stateTime);
    }


  }
  private void setUpLoadingAnimation() {
    loadingSheet = new Texture(Gdx.files.internal("app/assets/animations/loading_spritesheet.png"));
    loadingAnimation = LoadAnimation(loadingSheet);
  }

  @Override
  public void dispose() {
    batch.dispose();
    loadingSheet.dispose();
  }

  public class LoadingAnimation extends Image {
    private float frameDuration = 0.09f;
    private Animation<TextureRegion> animation;

    private TextureRegion[] textureRegion;

    public LoadingAnimation(Texture sheet) {

      textureRegion = new TextureRegion[cols*rows];
      TextureRegion[][] tmp = TextureRegion.split(sheet,sheet.getWidth()/cols, sheet.getHeight()/rows);


      int index = 0;
      for (int i = 0; i < rows; i++) {

        for (int j = 0; j < cols; j++) {
          textureRegion[index++] = tmp[i][j];
        }
      }

      animation = new Animation<>(frameDuration, textureRegion);
    }


    public void updateAnimation(float stateTime){
      batch.begin();
      TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
      batch.draw(currentFrame, this.getY(), this.getY());
      batch.end();
    }



  }
  private Animation LoadAnimation(Texture sheet) {


      loadRegion = new TextureRegion[cols*rows];
      TextureRegion[][] tmp = TextureRegion.split(sheet,sheet.getWidth()/cols, sheet.getHeight()/rows);


      int index = 0;
      for (int i = 0; i < rows; i++) {

        for (int j = 0; j < cols; j++) {
          loadRegion[index++] = tmp[i][j];
        }
      }
      return new Animation<TextureRegion>(0.07f, loadRegion);

  }


}
