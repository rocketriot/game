package bham.bioshock.client.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import bham.bioshock.client.Router;

public class LoadingScreen extends ScreenMaster {

  public LoadingScreen(Router router) {
    super(router);
    stage = new Stage(new ScreenViewport());
    batch = stage.getBatch();
  }
}
