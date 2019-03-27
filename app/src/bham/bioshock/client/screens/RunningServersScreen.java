package bham.bioshock.client.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.models.store.CommunicationStore;
import bham.bioshock.communication.client.ServerStatus;

public class RunningServersScreen extends ScreenMaster {

  CommunicationStore store;
  Table table;
  Animation<TextureRegion> loading;
  Image loadingImage;
  float animationTime = 0;
  float time = 0;
  
  public RunningServersScreen(CommunicationStore store, Router router, AssetContainer assets) {
    super(router, assets);
    this.store = store;
  }
  
  public void show() {
    super.show();

    loadingImage = new Image();
    loadingImage.setSize(200, 200);
    genAnimation();
    
    table = new Table();
    table.setFillParent(true);
    
    updateTable();
    drawBackButton();
    
    stage.addActor(table);
  }
  
  public void genAnimation() {
    Texture t = assets.get(Assets.loading, Texture.class);
    TextureRegion[][] list = TextureRegion.split(t, t.getWidth() / 26, t.getHeight());
    loading = Assets.textureToAnimation(list, 26, 0, 0.05f);
  }
  
  @Override
  public void render(float delta) {
    super.render(delta);
    animationTime += delta;
    time += delta;
    
    TextureRegion currentFrame = loading.getKeyFrame(animationTime, true);
    TextureRegionDrawable drawable = new TextureRegionDrawable(currentFrame);
    loadingImage.setDrawable(drawable);
    
    if(time > 1f) {
      time = 0;
      table.clearChildren();
      updateTable();      
    }
  }
 
  
  public void updateTable() {
    table.defaults().width(200);
    ServerStatus recovered = store.getRecoveredServer();
    
    if(store.getServers().size() == 0) {
      Table container = new Table();
      container.addActor(loadingImage);
      table.add(container).width(200).height(200).pad(20);
      table.row();
      Label label = new Label("Searching...", skin);
      table.add(label).width(label.getPrefWidth()).center();
      table.row();
    }   
    
    for(ServerStatus server : store.getServers()) {
      Label pl = new Label(server.getName(), skin);
      TextButton btn;
      if(recovered != null && recovered.getId().equals(server.getId())) {
         btn = new TextButton("Reconnect", skin);
         
         btn.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
             SoundController.playSound("menuSelect");
             server.setPlayerId(recovered.getPlayerId());
             router.call(Route.RECONNECT_RECOVERED, server);
           }
         });
         
      } else {
         btn = new TextButton("Connect", skin);        
      
         btn.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
             SoundController.playSound("menuSelect");
             showDialogue(server);
           }
         });
      }
      
      table.add(pl).padRight(50);
      table.add(btn);
      table.row();
      stage.addActor(table);
    }
  }
  
  /** Displays a dialogue to either host or join a game */
  private void showDialogue(ServerStatus server) {
    TextField textField = new TextField("", skin);

    Dialog dialog = new Dialog("", skin) {
      protected void result(Object object) {
        if (object.equals(false))
          return;
        
        String text = textField.getText();

        if (text.equals("")) {
          alert("Please enter your name");
          return;
        }

        SoundController.playSound("menuSelect");
        router.call(Route.CONNECT, server);
        router.call(Route.JOIN_SCREEN, text);
      }
    };

    dialog.text(new Label("Enter your name:", skin, "window"));
    dialog.getContentTable().add(textField);
    dialog.button("OK", true);
    dialog.button("Cancel", false);

    dialog.show(stage);
  }
}
