package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.screens.HowToScreen;
import bham.bioshock.common.models.store.Store;
import com.google.inject.Inject;

public class HowToController extends Controller {

  private AssetContainer assets;

  @Inject
  public HowToController(Store store, Router router, BoardGame game, AssetContainer assets) {
    super(store, router, game);
    this.assets = assets;
  }

  public void show() {
    setScreen(new HowToScreen(router, assets));
  }
}
