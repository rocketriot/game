package bham.bioshock.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import bham.bioshock.client.Router;

public class LoadingScreen extends ScreenMaster {



  public LoadingScreen(Router router) {
    super(router);
    stage = new Stage(new ScreenViewport());
    batch = stage.getBatch();


  }

  @Override
  public void show() {
    super.show();

  }

  @Override
  public void render(float delta) {
    super.render(delta);

  }


}
