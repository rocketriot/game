package bham.bioshock.client.screens;

import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.Renderer;
import com.badlogic.gdx.Screen;

public class MinigameScreen implements Screen {

  private Renderer renderer;

  public MinigameScreen(MinigameStore store, Router router) {
    this.renderer = new Renderer(store, router);
  }

  @Override
  public void show() {
    // TODO Auto-generated method stub

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
    // TODO Auto-generated method stub

  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub

  }

  @Override
  public void hide() {
    // TODO Auto-generated method stub

  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }
}
