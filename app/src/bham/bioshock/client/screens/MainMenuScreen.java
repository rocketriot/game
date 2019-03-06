package bham.bioshock.client.screens;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.SoundController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen extends ScreenMaster {

  // buttons
  private TextButton host;
  private TextButton howto;
  private TextButton preferences;
  private TextButton exit;
  private TextButton join;



  public MainMenuScreen(Router router) {
    super(router);
    // set the stage, which will react to user inputs
    stage = new Stage(new ScreenViewport());
    batch = new SpriteBatch();
    router.call(Route.START_MUSIC, "mainMenu");
  }

  @Override
  public void show() {
    super.show();
    drawButtons();
    addListeners();

  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void render(float delta) {
    super.render(delta);

  }




  private void drawButtons() {
    Container<Table> tableContainer = new Container<>();
    float container_width = screen_width * 0.8f;
    float container_height = screen_height * 0.9f;
    tableContainer.setSize(container_width, container_height);
    tableContainer.setPosition((screen_width - container_width) / 2.0f,
        (screen_height - container_height) / 2.0f);

    // Table to hold menu button, will change this to a better style
    Table table = new Table(skin);

    // adding buttons
    // skins to be styled later

    host = new TextButton("Host Game", skin);
    join = new TextButton("Join Game", skin);
    howto = new TextButton("How to Play", skin);
    preferences = new TextButton("Preferences", skin);
    exit = new TextButton("Exit", skin);

    // add the buttons to the table
    table.row();
    table.add(host).fillX().uniform();
    table.row();
    table.add(join).fillX().uniform();
    table.row();
    table.add(howto).fillX().uniform();
    table.row();
    table.add(preferences).fillX().uniform();
    table.row();
    table.add(exit).fillX().uniform();

    tableContainer.setActor(table);
    stage.addActor(tableContainer);
  }

  private void addListeners() {
    Router router = this.router;

    // add change listeners for the buttons
    exit.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        SoundController.playSound("menuSelect");
        Gdx.app.exit();
      }
    });

    host.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        /** Bring up a dialogue to ask the user for a host name then start the new server */
        SoundController.playSound("menuSelect");
        showHostDialogue();
      }
    });

    howto.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        SoundController.playSound("menuSelect");
        router.call(Route.HOW_TO);
      }
    });

    preferences.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        SoundController.playSound("menuSelect");
        router.call(Route.PREFERENCES);
      }
    });

    join.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        // Do something to add a new player...
        SoundController.playSound("menuSelect");
        showJoinDialogue();
      }
    });

  }

  private void showHostDialogue() {

    TextField textField = new TextField("", skin, "login");

    Dialog diag = new Dialog("Host Game", skin) {

      protected void result(Object object) {
        if (object.equals(true)) {
          String host_name = textField.getText();
          if (host_name.equals("")) {
            alert("Please Enter a Host Name");
          } else {
            // show join screen
            SoundController.playSound("menuSelect");
            router.call(Route.HOST_GAME, host_name);
          }
        } else {
          SoundController.playSound("menuSelect");
          System.out.println("Cancelled..");
        }
      }
    };

    diag.text(new Label("Please enter a host name", skin));
    diag.getContentTable().add(textField);
    diag.button("OK", true);
    diag.button("Cancel", false);

    diag.show(stage);
  }

  private void showJoinDialogue() {

    TextField textField = new TextField("", skin, "login");

    Dialog diag = new Dialog("Join Game", skin) {

      protected void result(Object object) {

        if (object.equals(true)) {
          String username = textField.getText();

          if (username.equals("")) {
            alert("Please Enter a Username");
          } else {
            SoundController.playSound("menuSelect");
            router.call(Route.JOIN_SCREEN, username);
          }

        } else {
          SoundController.playSound("menuSelect");
          System.out.println("Cancelled..");
        }
      }

    };

    diag.text(new Label("Please enter a username", skin));
    diag.getContentTable().add(textField);
    diag.button("OK", true);
    diag.button("Cancel", false);

    diag.show(stage);
  }





}
