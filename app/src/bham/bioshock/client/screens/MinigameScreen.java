package bham.bioshock.client.screens;

import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.Renderer;
import bham.bioshock.minigame.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class MinigameScreen implements Screen {

  private Renderer renderer;
  private MinigameStore localStore;
  private World world;

  public MinigameScreen(MinigameStore store) {
    this.localStore = store;
    this.world = store.getWorld();
    this.renderer = new Renderer(world);
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
