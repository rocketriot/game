package bham.bioshock.client.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;

/** The Loading screen. */
public class LoadingScreen extends ScreenMaster {

  /** The font. */
  private BitmapFont font;

  /** The text. */
  private String text;

  /** The Layout. */
  private GlyphLayout layout;

  /**
   * Instantiates a new Loading screen.
   *
   * @param router the router
   */
  public LoadingScreen(Router router, AssetContainer assets) {
    super(router, assets);
    stage = new Stage(new ScreenViewport());
    batch = stage.getBatch();
    text = "";
    layout = new GlyphLayout();
  }

  /**
   * Sets text.
   *
   * @param text the text
   */
  public void setText(String text) {
    this.text = text;
  }

  @Override
  public void show() {
    font = assets.getFont(60);
    super.show();
    drawBackButton();
  }

  @Override
  public void render(float delta) {
    super.render(delta);

    layout.setText(font, text);
    batch.begin();

    float fontX = screenWidth / 2 - layout.width / 2;
    float fontY = screenHeight / 2 - layout.height / 2;

    font.draw(batch, text, fontX, fontY);
    batch.end();
  }
  
  @Override
  public void hide() {
    stage.dispose();
  }
}
