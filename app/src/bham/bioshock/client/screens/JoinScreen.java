package bham.bioshock.client.screens;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.Store;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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

    router.call(Route.START_GAME);
  }

  private void buildJoinScreen() {
    table = new Table();
    table.setFillParent(true);
    //table.setHeight(screen_height * 0.7f);
    //table.setWidth(screen_width * 0.7f);
    table.pad(CELL_PADDING);
    stage.addActor(table);
    
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

  @Override
  public void render(float delta) {
    ArrayList<Player> players = store.getPlayers();
    
    for(int i=0; i<4; i++) {
      if(players.size() > i) {
        Player p = players.get(i);        
        containers[i].waitingLabel.setText(WaitText.CONNECTED.text);
        containers[i].nameLabel.setText(p.getUsername());
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
