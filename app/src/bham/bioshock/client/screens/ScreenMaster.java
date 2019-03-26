package bham.bioshock.client.screens;

import bham.bioshock.Config;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.controllers.SoundController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class ScreenMaster implements Screen {
  protected Stage stage;
  protected Batch batch;
  protected Stack stack;
  protected FitViewport viewport;
  protected Router router;

  protected float screenWidth;
  protected float screenHeight;

  protected Texture background;

  protected TextButton backButton;

  protected Skin skin = new Skin(Gdx.files.internal(Assets.skin));

  public ScreenMaster(Router router) {
    this.router = router;
    
    viewport = new FitViewport(Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT);
    stage = new Stage(viewport);
    batch = new SpriteBatch();
    
    this.screenWidth = Gdx.graphics.getWidth();
    this.screenHeight = Gdx.graphics.getHeight();
    
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);
    
    // Create background
    background = new Texture(Gdx.files.internal(Assets.menuBackground));
  }

  protected void drawBackground() {
    batch.begin();
    batch.disableBlending();
    batch.draw(background, 0, 0, screenWidth, screenHeight);
    batch.enableBlending();
    batch.end();
  }

  /** Adds a button that takes the user back to the previous screen */
  protected void drawBackButton() {
    backButton = new TextButton("Back", skin);
    stage.addActor(backButton);

    setPrevious();
  }

  protected void setPrevious() {
    backButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        SoundController.playSound("menuSelect");
        router.back();
      }
    });
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    batch.getProjectionMatrix().setToOrtho2D(0, 0, screenWidth, screenHeight);
    
    drawBackground();
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
    stage.act(Gdx.graphics.getDeltaTime());

    screenWidth = Gdx.graphics.getWidth();
    screenHeight = Gdx.graphics.getHeight();
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    stage.dispose();
    batch.dispose();
  }

  public void alert(String alert_text) {
    Dialog diag = new Dialog("", skin);
    diag.text(new Label(alert_text, skin, "window"));
    diag.button("OK", true);
    diag.show(stage);
  }

  /** Generates an asset given an asset and screen coordinates */
  protected Image drawAsset(String asset, int x, int y) {
    // Generate texture
    Texture texture = new Texture(asset);
    texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    // Generate image
    Image image = new Image(texture);
    image.setPosition(x, y);

    // Add to screen
    stage.addActor(image);

    return image;
  }
}
