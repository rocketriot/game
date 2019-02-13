package bham.bioshock.client.screens;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.Store;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class JoinScreen extends ScreenMaster {
  
  private Store store;
  
  private final int CELL_PADDING = 100;
  private final int IMAGE_WIDTH = 20;

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
      Texture t = new Texture(Gdx.files.internal("app/assets/entities/rockets/"+(i+1)+".png"));
      containers[i] = new PlayerContainer(t, "Player"+(i+1), WaitText.WAITING);      
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
        router.call(Route.START_GAME);
      }
    });
    
    miniGameButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        router.call(Route.START_MINIGAME);
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
    ArrayList<Player> players = store.getPlayers();
    
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
    
    super.render(delta);
  }

  
  private class PlayerContainer extends Container<Table> {

    String waitText;
    Label waitingLabel;
    Label nameLabel;

    PlayerContainer(Texture img, String name, WaitText text) {
      this.waitText = text.text;
      Image image = new Image(img);
      image.setScaling(Scaling.fit);

      Table t = new Table();
      t.setFillParent(true);
      t.pad(15);

      nameLabel = new Label(name, skin);
      waitingLabel = new Label(waitText, skin);

      t.add(nameLabel).pad(0,0,10,0);
      t.row();
      t.add(image).expand();
      t.row();
      t.add(waitingLabel).pad(10,0,0,0);
      this.setActor(t);
    }
  }


}
