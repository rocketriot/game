package bham.bioshock.client.controllers;

import bham.bioshock.client.screens.HowToScreen;
import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Store;

public class HowToController extends Controller {
  
  @Inject
  public HowToController(Store store, Router router, BoardGame game) {
    super(store, router, game);
  }

  public void show() {
    setScreen(new HowToScreen(router));
  }
}
