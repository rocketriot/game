package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.EndScreen;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.server.Server;
import com.google.inject.Inject;

public class EndGameController extends Controller {
  private Server server;

  @Inject
  public EndGameController(Store store, Router router, BoardGame game,Server server) {
    super(store, router, game);
    this.server = server;
  }

  public void show() {
    server.stop();
    store.reconnecting(false);
    setScreen(new EndScreen(router,store));
  }
}
