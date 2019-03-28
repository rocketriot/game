package bham.bioshock.client;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.screens.InitLoadingScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.google.inject.Singleton;

@Singleton
public class BoardGame extends Game {

  private Router router;
  private AssetContainer assets;

  /**
   * Saves the router
   *
   * @param router
   */
  public void addRouter(Router router) {
    this.router = router;
  }

  /**
   * Saves asset container
   *
   * @param asset container
   */
  public void addAssetContainer(AssetContainer assets) {
    this.assets = assets;
  }

  /** Called when the game is ready to start */
  @Override
  public void create() {
    Gdx.graphics.setTitle("Rocket Riot");
    setScreen(new InitLoadingScreen(router, assets));
  }

  /** Free memory */
  @Override
  public void dispose() {
    assets.clear();
    super.dispose();
  }
}
