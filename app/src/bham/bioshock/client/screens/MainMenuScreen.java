package bham.bioshock.client.screens;

import bham.bioshock.Config;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.controllers.SoundController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class MainMenuScreen extends ScreenMaster {
  private Image logo;
  private Image hostButton;
  private Image joinButton;
  private Image howToPlayButton;
  private Image preferencesButton;
  private Image exitButton;

  public MainMenuScreen(Router router) {
    super(router);

    Pixmap pm = new Pixmap(Gdx.files.internal(Assets.cursor));
    Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
    pm.dispose();

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


  private void addListener(Image image, BaseClickListener listener) {
    image.addListener(listener);
  }

  class BaseClickListener extends ClickListener {
    private Image image;
    private SpriteDrawable normal;
    private SpriteDrawable hover;

    public BaseClickListener(Image image, String normal, String hover) {
      Texture normalTexture = new Texture(normal);
      normalTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

      Texture hoverTexture = new Texture(hover);
      hoverTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

      this.image = image;
      this.normal = new SpriteDrawable(new Sprite(normalTexture));
      this.hover = new SpriteDrawable(new Sprite(hoverTexture));
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
      image.setDrawable(hover);
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
      image.setDrawable(normal);
    }
  }


  private void drawButtons() {
    logo = drawAsset(Assets.logo, (Config.GAME_WORLD_WIDTH / 2) - 258, Config.GAME_WORLD_HEIGHT - 550);

    hostButton = drawAsset(Assets.hostButton, 200, 300);
    addListener(hostButton, new BaseClickListener(hostButton, Assets.hostButton, Assets.hostButtonHover) {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");

        showDialogue("host");
        showDialog2();

      }
    });

    joinButton = drawAsset(Assets.joinButton, 725, 50);
    addListener(joinButton, new BaseClickListener(joinButton, Assets.joinButton, Assets.joinButtonHover) {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        router.call(Route.SERVERS_SCREEN);
      }
    });

    howToPlayButton = drawAsset(Assets.howToPlayButton, 1200, 250);
    addListener(howToPlayButton, new BaseClickListener(howToPlayButton, Assets.howToPlayButton, Assets.howToPlayButtonHover) {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        router.call(Route.HOW_TO);
      }
    });

    preferencesButton = drawAsset(Assets.preferencesButton, Config.GAME_WORLD_WIDTH - 427, 0);
    addListener(preferencesButton, new BaseClickListener(preferencesButton, Assets.preferencesButton, Assets.preferencesButtonHover) {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        SoundController.playSound("menuSelect");
        router.call(Route.PREFERENCES);
      }
    });

    exitButton = drawAsset(Assets.exitButton, 0, 0);
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

  private void showDialog2() {
    TextField textField = new TextField("", skin);

    Dialog dialog = new Dialog("", skin) {
      protected void result(Object object) {

        if (object.equals(false))
          return;

        String text = textField.getText();
        String regex = "[0-9]+";

        if (!text.matches(regex)) {
          alert("Please enter a valid number of rounds");

          return;
        }
        SoundController.playSound("menuSelect");
      }
    };
    dialog.text(new Label("Enter the rounds:", skin, "window"));
    dialog.getContentTable().add(textField);
    dialog.button("OK", true);
    dialog.button("Cancel", false);

    dialog.show(stage);
  }
}
