package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.screens.MainMenuScreen;
import bham.bioshock.client.screens.ScreenMaster;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.client.CommunicationClient;
import bham.bioshock.communication.client.ServerStatus;
import bham.bioshock.server.Server;
import com.google.inject.Inject;

public class MainMenuController extends Controller {

  Server server;
  BoardGame game;
  CommunicationClient commClient;
  AssetContainer assets;

  @Inject
  public MainMenuController(
      Store store,
      Router router,
      BoardGame game,
      Server server,
      CommunicationClient commClient,
      AssetContainer assets) {
    super(store, router, game);
    this.server = server;
    this.assets = assets;
    this.game = game;
    this.commClient = commClient;
  }

  /**
   * Starts a server and opens the join screen
   *
   * @param hostName
   */
  public void hostGame(String hostName) {
    if (server.start(hostName)) {
      store.setHost(true);
      ServerStatus s = new ServerStatus(hostName, "localhost", server.getId().toString());
      router.call(Route.CONNECT, s);
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
    store.getCommStore().clearServers();
    commClient.stopDiscovery();
    commClient.disconnect();
    store.reset();

    setScreen(new MainMenuScreen(router, assets));
  }

  public void saveTurns(int number) {
    store.setMaxRounds(number);
  }

  public void alert(String message) {
    ((ScreenMaster) store.getScreen()).alert(message);
  }
}
