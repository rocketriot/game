package bham.bioshock.client.screens;

import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.Renderer;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

public class MinigameScreen implements Screen {

  private Renderer renderer;
  private boolean loading = true;
  private AssetManager manager;
  private World world;

  private LoadingScreen loadingScreen;
  
  public MinigameScreen(Store store, Router router) {
    this.loadingScreen = new LoadingScreen(router);
    manager = new AssetManager();
    world = store.getMinigameStore().getWorld();
    this.renderer = new Renderer(store, router, manager);
  }


  @Override
  public void show() {
    loadingScreen.show();
    
    Astronaut.loadTextures(manager);
    Rocket.loadTextures(manager);
    Gun.loadTextures(manager);
    Bullet.loadTextures(manager);
    Flag.loadTextures(manager);
    Goal.loadTextures(manager);
    World.loadTextures(manager, world.getTextureId());
    Platform.loadTextures(manager, world.getTextureId());
  }


  @Override
  public void render(float delta) {
    if(loading && manager.update()) {
      // Loading done
      loading = false;
      renderer.show();
    }
    
    if(loading) {
      // Update loading progress
      float progress = manager.getProgress();
      loadingScreen.setText( ((int) Math.floor(progress * 100))+"%" );
      loadingScreen.render(delta);
    } else {
      // Render game
      renderer.render(delta);      
    }
  }

  @Override
  public void resize(int width, int height) {
    loadingScreen.resize(width, height);
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
    manager.clear();
    renderer.dispose();
  }

  @Override
  public void dispose() {
    renderer.dispose();
  }
}
