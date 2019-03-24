package bham.bioshock.client.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Router;


public class LoadingScreen extends ScreenMaster {
  
  private BitmapFont font;
  private int progress;
  GlyphLayout layout;
  
  
  public LoadingScreen(Router router) {
    super(router);
    stage = new Stage(new ScreenViewport());
    batch = stage.getBatch();
    progress = 0;
    layout = new GlyphLayout();
  }

  public void setProgress(int p) {
    progress = p;
  }
  
  @Override
  public void show() {
    FontGenerator fontGenerator = new FontGenerator();
    font = fontGenerator.generate(30, Color.WHITE);
    super.show();
  }

  @Override
  public void render(float delta) {
    super.render(delta);
    
    float x = screenWidth / 2;
    float y = screenHeight / 2;

    String text = progress + "%";
    layout.setText(font, text);
    batch.begin();
    font.draw(batch, text, x - layout.width, y - layout.height);
    batch.end();
  }

}
