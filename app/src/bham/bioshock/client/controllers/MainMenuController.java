package bham.bioshock.client.controllers;

import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.MainMenuScreen;
import bham.bioshock.common.models.Store;
import bham.bioshock.server.Server;

public class MainMenuController extends Controller {

  Server server;
  BoardGame game;

  @Inject
  public MainMenuController(Store store, Router router, BoardGame game, Server server) {
    super(store, router);
    this.server = server;
    this.game = game;
  }

  /** Creates a server */
  private void startServer() {
    if(!server.isAlive()) {
      server.start();      
    }
  }

  public void hostGame(String hostName) {
    startServer();
    
    router.call(Route.JOIN_SCREEN, hostName);
  }
  
  /** Renders main menu */
  public void show() {
    game.setScreen(new MainMenuScreen(router));
  }
}
