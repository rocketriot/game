package bham.bioshock.client.screens;

import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Map;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.Renderer;
import bham.bioshock.minigame.models.Bullet;
import com.badlogic.gdx.Screen;

public class MinigameScreen implements Screen {

  private Renderer renderer;

<<<<<<< HEAD
  public MinigameScreen(MinigameStore store, Router router, Map map) {
    this.renderer = new Renderer(store, router, map);
=======
  public MinigameScreen(Store store, Router router) {
    this.renderer = new Renderer(store, router);
>>>>>>> master
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
  
  public void addBullet(Bullet b) {
    renderer.addBullet(b);
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
