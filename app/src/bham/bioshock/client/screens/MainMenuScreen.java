package bham.bioshock.client.screens;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.SoundController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainMenuScreen extends ScreenMaster {
  private TextButton host;
  private TextButton howto;
  private TextButton preferences;
  private TextButton exit;
  private TextButton join;

  public MainMenuScreen(Router router) {
    super(router);

    router.call(Route.START_MUSIC, "mainMenu");
  }

  @Override
  public void show() {
    super.show();

    drawButtons();
  }

  @Override
  public void render(float delta) {
    super.render(delta);
  }

  private void drawButtons() {
    Container<Table> tableContainer = new Container<>();
    tableContainer.setFillParent(true);
    tableContainer.center();

    // Table to hold menu button, will change this to a better style
    Table table = new Table(skin);

    host = new TextButton("Host Game", skin);
    join = new TextButton("Join Game", skin);
    howto = new TextButton("How to Play", skin);
    preferences = new TextButton("Preferences", skin);
    exit = new TextButton("Exit", skin);

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
    
    addListeners();
  }

  private void addListeners() {
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
        /**
         * Bring up a dialogue to ask the user for a host name then start the new server
         */
        SoundController.playSound("menuSelect");
        showDialogue("host");
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
        SoundController.playSound("menuSelect");
        showDialogue("join");
      }
    });
  }

  /** Displays a dialogue to either host or join a game */
  private void showDialogue(String type) {
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
        

        if (type.equals("host")) router.call(Route.HOST_GAME, text);
        if (type.equals("join")) router.call(Route.JOIN_SCREEN, text);
      }
    };

    dialog.text(new Label("Enter your name:", skin, "window"));
    dialog.getContentTable().add(textField);
    dialog.button("OK", true);
    dialog.button("Cancel", false);

    dialog.show(stage);
  }
}
