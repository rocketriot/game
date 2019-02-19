package bham.bioshock.client.controllers;

import bham.bioshock.client.screens.ScreenMaster;
import bham.bioshock.client.screens.StatsContainer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import bham.bioshock.common.models.store.Store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.MainMenuScreen;
import bham.bioshock.server.Server;

public class MainMenuController extends Controller {

  Server server;
  BoardGame game;

  @Inject
  public MainMenuController(Store store, Router router, BoardGame game, Server server) {
    super(store, router, game);
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
    setScreen(new MainMenuScreen(router));
  }

  
  public void alert(String message) {
    ((ScreenMaster)store.getScreen()).alert(message);
  }
}
