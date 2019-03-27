package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.screens.EndScreen;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.server.Server;
import com.google.inject.Inject;

public class EndGameController extends Controller {
  
  private Server server;
  private AssetContainer assets;
  
  @Inject
  public EndGameController(Store store, Router router, BoardGame game,Server server, AssetContainer assets) {
    super(store, router, game);
    this.server = server;
    this.assets = assets;
  }

  public void show() {
    //server.stop();
   // store.reconnecting(false);
    setScreen(new EndScreen(router,store, assets));
  }
}
