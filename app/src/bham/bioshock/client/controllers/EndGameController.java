package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.screens.EndScreen;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.server.Server;
import com.google.inject.Inject;

/** Controller for the end game screen */
public class EndGameController extends Controller {
  /** Stores assets of the game */
  private AssetContainer assets;
  
  @Inject
  public EndGameController(Store store, Router router, BoardGame game,Server server, AssetContainer assets) {
    super(store, router, game);
    this.assets = assets;
  }

  /** Shows the end game screen */
  public void show() {
    setScreen(new EndScreen(router,store, assets));
  }
}
