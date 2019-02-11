package bham.bioshock.client.screens;

import bham.bioshock.client.controllers.LoadingController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LoadingScreen extends ScreenMaster {
  LoadingController controller;

  public LoadingScreen(LoadingController controller) {
    this.controller = controller;

    stage = new Stage(new ScreenViewport());
    batch = stage.getBatch();
  }
}
