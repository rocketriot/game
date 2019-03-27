package bham.bioshock.client.screens;

import bham.bioshock.Config;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.assets.Assets.GamePart;
import bham.bioshock.client.controllers.SoundController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class MainMenuScreen extends ScreenMaster {

  public MainMenuScreen(Router router, AssetContainer assets) {
    super(router, assets);
    router.call(Route.START_MUSIC, "mainMenu");
  }

  @Override
  public void show() {
    super.show();
    assets.dispose(GamePart.BOARDGAME);

    drawButtons();
  }

  @Override
  public void render(float delta) {
    super.render(delta);
  }

  private void drawButtons() {
    drawAsset(Assets.logo, (Config.GAME_WORLD_WIDTH / 2) - 258, Config.GAME_WORLD_HEIGHT - 550);

    Image hostButton = drawAsset(Assets.hostButton, 200, 300);
    addListener(hostButton, new BaseClickListener(hostButton, Assets.hostButton, Assets.hostButtonHover) {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");

        showDialogue();
      }
    });

    Image joinButton = drawAsset(Assets.joinButton, 725, 50);
    addListener(joinButton, new BaseClickListener(joinButton, Assets.joinButton, Assets.joinButtonHover) {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        router.call(Route.SERVERS_SCREEN);
      }
    });

    Image howToPlayButton = drawAsset(Assets.howToPlayButton, 1200, 250);
    addListener(howToPlayButton, new BaseClickListener(howToPlayButton, Assets.howToPlayButton, Assets.howToPlayButtonHover) {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        router.call(Route.HOW_TO);
      }
    });

    Image preferencesButton = drawAsset(Assets.preferencesButton, Config.GAME_WORLD_WIDTH - 427, 0);
    addListener(preferencesButton, new BaseClickListener(preferencesButton, Assets.preferencesButton, Assets.preferencesButtonHover) {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        router.call(Route.PREFERENCES);
      }
    });

    Image exitButton = drawAsset(Assets.exitButton, 0, 0);
    addListener(exitButton, new BaseClickListener(exitButton, Assets.exitButton, Assets.exitButtonHover) {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        Gdx.app.exit();
      }
    });
  }

  /**
   * Displays a dialogue to either host or join a game
   */
  private void showDialogue() {
    TextField textField = new TextField("", skin);
    TextField roundsField = new TextField("20", skin);

    Dialog dialog = new Dialog("", skin) {
      
      protected void result(Object object) {
        if (object.equals(false))
          return;
        
        String text = textField.getText();

        if (text.equals("")) {
          alert("Please enter your name");
          return;
        }
        
        String rounds = roundsField.getText();
        String regex = "[0-9]+";

        if (rounds.matches(regex)) {
          try {
            Integer num = Integer.parseInt(rounds);
            router.call(Route.SELECTED_TURNS, num);
            router.call(Route.HOST_GAME, text);
            
            return;
          } catch (NumberFormatException e) {}
        }
        // Number incorrect
        alert("Please enter a valid number of rounds");

        SoundController.playSound("menuSelect");
      }
      
    };

    dialog.text(new Label("Enter your name:", skin, "window"));
    dialog.getContentTable().add(textField).width(250).pad(10);
    dialog.getContentTable().row();
    dialog.text(new Label("Enter number of rounds:", skin, "window"));
    dialog.getContentTable().add(roundsField).width(150).pad(10);
    dialog.row();
    
    dialog.button("OK", true);
    dialog.button("Cancel", false);

    dialog.show(stage);

    stage.setKeyboardFocus(textField);
  }
}
