package bham.bioshock.client.screens;

import bham.bioshock.client.Router;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class JoinScreen extends ScreenMaster {

  public JoinScreen(Router router) {
    super(router);
    stage = new Stage(new ScreenViewport());
    batch = new SpriteBatch();
  }

  @Override
  public void show() {
    super.show();
  }
}
