package bham.bioshock.client.screens;

import bham.bioshock.client.Assets;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.consts.Config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends ScreenMaster {
  private Image logo;
  private Image hostButton;
  private Image joinButton;
  private Image howToPlayButton;
  private Image preferencesButton;
  private Image exitButton;

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

  /** Generates an asset given an asset and screen coordinates */
  private Image drawAsset(String asset, int x, int y, ClickListener clickListener) {
    // Generate texture
    Texture texture = new Texture(asset);
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

    // Generate image
    Image image = new Image(texture);
    image.setPosition(x, y);

    if (clickListener != null)
      image.addListener(clickListener);

    // Add to screen
    stage.addActor(image);

    return image;
  }

  private void drawButtons() {
    logo = drawAsset(Assets.logo, (Config.GAME_WORLD_WIDTH / 2) - 258, Config.GAME_WORLD_HEIGHT - 550, null);

    hostButton = drawAsset(Assets.hostButton, 200, 300, new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        showDialogue("host");
      }
    });

    joinButton = drawAsset(Assets.joinButton, 725, 50, new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        showDialogue("join");
      }
    });

    howToPlayButton = drawAsset(Assets.howToPlayButton, 1200, 250, new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        router.call(Route.HOW_TO);
      }
    });

    preferencesButton = drawAsset(Assets.preferencesButton, Config.GAME_WORLD_WIDTH - 427, 0, new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        router.call(Route.PREFERENCES);
      }
    });

    exitButton = drawAsset(Assets.exitButton, 0, 0, new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        Gdx.app.exit();
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

        if (type.equals("host"))
          router.call(Route.HOST_GAME, text);
        if (type.equals("join"))
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
