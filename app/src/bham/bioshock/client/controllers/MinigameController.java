package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.MinigameScreen;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.World;
import com.google.inject.Inject;

public class MinigameController extends Controller {

  @Inject
  public MinigameController(Store store, Router router, BoardGame game) {
    super(store, router, game);
  }

  public void show() {
    // Create local store for the minigame, and create a new world
    MinigameStore localStore = new MinigameStore();
    localStore.setWorld(new World());
    store.setMinigameStore(localStore);

    setScreen(new MinigameScreen(localStore));
  }
}
