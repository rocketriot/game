package bham.bioshock.client.screens;

import bham.bioshock.client.Assets;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.SoundController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public abstract class ScreenMaster implements Screen {
  protected Stage stage;
  protected Batch batch;
  protected Stack stack;
  protected Router router;

  protected float screen_width;
  protected float screen_height;

  protected Texture background;

  protected TextButton backButton;

  protected Skin skin = new Skin(Gdx.files.internal(Assets.skin));

  public ScreenMaster(Router router) {
    this.router = router;
    this.screen_width = Gdx.graphics.getWidth();
    this.screen_height = Gdx.graphics.getHeight();
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);
    
    // Create background
    background = new Texture(Gdx.files.internal("app/assets/backgrounds/menu.png"));

    // drawBackground();
  }

  protected void drawBackground() {
    // render background
    // clear the screen
    Gdx.gl.glClearColor(0, 0, 0, 0);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();
    batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
    drawBackground();
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
    stage.act(Gdx.graphics.getDeltaTime());

    screen_width = Gdx.graphics.getWidth();
    screen_height = Gdx.graphics.getHeight();
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

    Dialog diag = new Dialog("", skin) {

      protected void result(Object object) {

        if (object.equals(true)) {

        } else {

        }
      }

    };

    diag.text(new Label(alert_text, skin, "window"));
    diag.button("OK", true);

    diag.show(stage);
  }
}
