package bham.bioshock.client.screens;

import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.assets.Assets.GamePart;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.Renderer;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

/**
 * Minigame screen loading the assets and running the minigame renderer
 */
public class MinigameScreen implements Screen {

  /** Minigame renderer */
  private Renderer renderer;
  /** Is loading */
  private boolean loading = true;
  /** Assets container */
  private AssetContainer assets;
  /** Minigame world */
  private World world;
  /** Loading screen */
  private LoadingScreen loadingScreen;
  
  /**
   * Create minigame screen
   * 
   * @param store
   * @param router
   * @param assets
   */
  public MinigameScreen(Store store, Router router, AssetContainer assets) {
    this.loadingScreen = new LoadingScreen(router, assets);
    this.assets = assets;
    world = store.getMinigameStore().getWorld();
    this.renderer = new Renderer(store, router, assets);
  }


  /**
   * Load all needed assets
   */
  @Override
  public void show() {
    loadingScreen.show();
    
    Astronaut.loadTextures(assets);
    Rocket.loadTextures(assets);
    Gun.loadTextures(assets);
    Bullet.loadTextures(assets);
    Flag.loadTextures(assets);
    Goal.loadTextures(assets);
    World.loadTextures(assets, world.getTextureId());
    Platform.loadTextures(assets, world.getTextureId());
  }

  @Override
  public void render(float delta) {
    if(loading && assets.update()) {
      // Loading done
      loading = false;
      loadingScreen.hide();
      renderer.show();
    }
    
    if(loading) {
      // Update loading progress
      float progress = assets.getProgress();
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
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {
    // Dispose minigame assets
    assets.dispose(GamePart.MINIGAME);
    renderer.dispose();
    
  }

  @Override
  public void dispose() {
    renderer.dispose();
  }
}
