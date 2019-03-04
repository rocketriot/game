package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.ClientHandler;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.JoinScreen;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.client.CommunicationClient;
import com.google.inject.Inject;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JoinScreenController extends Controller {

  private static final Logger logger = LogManager.getLogger(JoinScreenController.class);

  private ClientService clientService;
  private CommunicationClient commClient;
  private ClientHandler clientHandler;

  @Inject
  public JoinScreenController(Store store, Router router, BoardGame game,
      CommunicationClient commClient, ClientHandler clientHandler) {
    super(store, router, game);
    this.clientHandler = clientHandler;
    this.commClient = commClient;
    this.game = game;
  }

  public void show(String username) {
    // Create a new player
    Player player = new Player(username);

    // Save player to the store
    store.setMainPlayer(player);

    // Create connection to the server
    try {
      connectToServer(player);
      setScreen(new JoinScreen(router, store));

    } catch (ConnectException e) {
      // No server started
      logger.error(e.getMessage());
      router.call(Route.ALERT, e.getMessage());
    }
  }

  public void disconnectPlayer() {
    commClient.getConnection().send(new Action(Command.REMOVE_PLAYER));
  }

  public void removePlayer(UUID id) {
    store.removePlayer(id);
  }

  /**
   * Handle when the server tells us a new player was added to the game
   */
  public void addPlayer(ArrayList<Player> players) {
    for (Player player : players) {
      logger.debug("Player: " + player.getUsername() + " connected");
      store.addPlayer(player);
    }
  }

  /**
   * Create a connection with the server and wait in lobby when a username is entered
   */
  public void connectToServer(Player player) throws ConnectException {
    // Create server connection
    clientService = commClient.connect(player.getUsername());
    clientService.registerHandler(clientHandler);

    // Add the player to the server
    clientService.send(new Action(Command.ADD_PLAYER, player));
  }

  /**
   * Handle when the server tells the client to start the game
   */
  public void start() {
    commClient.getConnection().send(new Action(Command.START_GAME));
    logger.debug("Ready to start! Waiting for the board");
  }
}
