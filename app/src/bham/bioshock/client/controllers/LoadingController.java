package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.screens.LoadingScreen;
import bham.bioshock.common.models.store.Store;
import com.google.inject.Inject;

public class LoadingController extends Controller {

  private AssetContainer assets;
  
  @Inject
  public LoadingController(Store store, Router router, BoardGame game, AssetContainer assets) {
    super(store, router, game);
    this.assets = assets;
  }
  
  public void show(String text) {
    LoadingScreen screen = new LoadingScreen(router, assets);
    screen.setText(text);
    setScreen(screen);
  }
  
  public void reconnect(Boolean v) {
    store.reconnecting(v);
  }
  
}
