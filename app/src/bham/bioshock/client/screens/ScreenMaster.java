package bham.bioshock.client.screens;

import bham.bioshock.client.Router;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
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

  protected BitmapFont font12;
  protected BitmapFont font18;

  protected TextButton backButton;

  protected Skin skin = new Skin(Gdx.files.internal("app/assets/skins/neon/skin/neon-ui.json"));

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

    setupFonts();

    addBackButton();
    // set the back button to take you to main menu - for now
    setPrevious();
    // drawBackground();
  }

  /** Set's up all the fonts needed for the screen */
  private void setupFonts() {
    FileHandle fontSource = Gdx.files.internal("app/assets/fonts/BebasNeueThin.otf");
    font12 = generateFont(fontSource, 12);
    font18 = generateFont(fontSource, 18);
  }

  /** Generates a bitmap font from source */
  private BitmapFont generateFont(FileHandle source, int size) {
    // Specify font size
    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    parameter.size = size;

    // Generate font
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(source);
    BitmapFont font = generator.generateFont(parameter);
    generator.dispose();

    return font;
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

  protected void addBackButton() {
    // add a button that takes the user back to the previous screen
    backButton = new TextButton("Back", skin);
    stage.addActor(backButton);
  }

  protected void setPrevious() {
    backButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        router.back();
      }
    });
  }

  @Override
  public void render(float delta) {
    drawBackground();
    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
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

    diag.text(new Label(alert_text, skin));
    diag.button("OK", true);
    // diag.button("Cancel", false);

    diag.show(stage);
  }
}
