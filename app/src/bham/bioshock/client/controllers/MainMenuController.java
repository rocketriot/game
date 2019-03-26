package bham.bioshock.client.controllers;

import bham.bioshock.client.screens.ScreenMaster;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.audio.Sound;
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

  /**
   * Starts a server and opens the join screen
   * 
   * @param hostName
   */
  public void hostGame(String hostName) {
    if (server.start()) {
      store.setHost(true);
      router.call(Route.JOIN_SCREEN, hostName);
    } else {
      alert("Server cannot be created.\nCheck if one is not already running");
    }
  }

  /** Renders main menu */
  public void show() {
    server.stop();
    store.setHost(false);
    store.reconnecting(false);
    setScreen(new MainMenuScreen(router));
  }

  public void saveTurns(String number){
    this.store.setMaxRounds(Integer.parseInt(number));
  }

  public void alert(String message) {
    ((ScreenMaster) store.getScreen()).alert(message);
  }
}
