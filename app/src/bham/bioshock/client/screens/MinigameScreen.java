package bham.bioshock.client.screens;

import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.Renderer;
import com.badlogic.gdx.Screen;

public class MinigameScreen implements Screen {

  private Renderer renderer;
  public MinigameScreen(Store store, Router router) {
    this.renderer = new Renderer(store, router);
  }

  @Override
  public void show() {

  }


  @Override
  public void render(float delta) {
    renderer.render(delta);
  }

  @Override
  public void resize(int width, int height) {
    renderer.resize(width, height);
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void dispose() {

  }
}
