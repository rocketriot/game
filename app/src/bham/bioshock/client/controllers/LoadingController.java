package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.LoadingScreen;
import bham.bioshock.common.models.store.Store;
import com.google.inject.Inject;

public class LoadingController extends Controller {

  
  @Inject
  public LoadingController(Store store, Router router, BoardGame game) {
    super(store, router, game);
  }
  
  public void show() {
    setScreen(new LoadingScreen(router));
  }
  
  public void reconnect(Boolean v) {
    store.reconnecting(v);
  }
  
}
