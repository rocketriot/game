package bham.bioshock.client.controllers;

import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.MainMenuScreen;
import bham.bioshock.common.models.Store;
import bham.bioshock.server.Server;
import bham.bioshock.client.screens.JoinScreen;
import bham.bioshock.common.models.Player;
import java.net.ConnectException;

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
  private void createServer() {
    server.start();
  }

  /** Renders main menu */
  public void show() {
    createServer();

    game.setScreen(new MainMenuScreen(router));
  }

  /** Creates a server and send the player to the join screen */
  public void createServer(String username) {
    client.createHostingServer();
    addPlayerToConnection(username);
  }

  public void addPlayerToConnection(String username) {
    // get the join screen controller
    JoinScreenController jsc = (JoinScreenController) client.getController(View.JOIN_SCREEN);
    try {
      jsc.connectToServer(username);

      // jsc.startGame();
    } catch (ConnectException e) {
      // Handle connection error
      System.err.println("CONNECTION ERROR");
    }

    changeScreen(View.JOIN_SCREEN);
  }
}
