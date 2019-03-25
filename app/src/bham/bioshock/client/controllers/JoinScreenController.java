package bham.bioshock.client.controllers;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.ClientHandler;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.JoinScreen;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.JoinScreenStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.client.CommunicationClient;
import bham.bioshock.communication.messages.boardgame.StartGameMessage;
import bham.bioshock.communication.messages.joinscreen.JoinScreenMoveMessage;
import bham.bioshock.communication.messages.joinscreen.ReconnectMessage;
import bham.bioshock.communication.messages.joinscreen.RegisterMessage;
import bham.bioshock.communication.messages.joinscreen.AddPlayerMessage.JoiningPlayer;

public class JoinScreenController extends Controller {

  private static final Logger logger = LogManager.getLogger(JoinScreenController.class);

  private CommunicationClient commClient;
  private ClientHandler clientHandler;
  private BoardGame game;

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

    store.setJoinScreenStore(new JoinScreenStore());
    // Create connection to the server
    try {
      connectToServer(player);
      setScreen(new JoinScreen(router, store, player));
    } catch (ConnectException e) {
      // Server cannot be started
      logger.error(e.getMessage());
      router.call(Route.ALERT, e.getMessage());
    }
  }
  
  public void disconnect() {
    commClient.disconnect();
    store.removeAllPlayers();
    router.back();
  }
  
  public void sendReconnect() {
    Optional<ClientService> clientService = commClient.getConnection();
    if(clientService.isPresent()) {
      clientService.get().registerHandler(clientHandler);
      UUID playerId = store.getMainPlayer().getId();
      clientService.get().send(new ReconnectMessage(playerId));
    }
  }

  public void removePlayer(UUID id) {
    if(JoinScreen.class.isInstance(game.getScreen())) {
      ((JoinScreen) game.getScreen()).removePlayer(id);    
      // Remove the player only in the join screen
      // If the game is running AI should take over
      store.removePlayer(id);
    }
  }

  /**
   * Handle when the server tells us a new player was added to the game
   */
  public void addPlayer(ArrayList<JoiningPlayer> players) {
    for (JoiningPlayer p : players) {
      logger.debug("Player: " + p.username + " connected");
      Player player = new Player(p.playerId, p.username, p.isCpu);
      player.setTextureID(p.textureId);
      store.addPlayer(player);
      if(JoinScreen.class.isInstance(game.getScreen())) {
        ((JoinScreen) game.getScreen()).addPlayer(player);
      } else {
        logger.fatal("Player can't be added because JoinScreen is not shown");
      }
    }
  }

  /**
   * Create a connection with the server and wait in lobby when a username is entered
   */
  public void connectToServer(Player player) throws ConnectException {
    // Create server connection
    ClientService service = commClient.connect();
    service.registerHandler(clientHandler);
    
    commClient.startReconnectionThread(router);

    // Add the player to the server
    service.send(new RegisterMessage(player));
  }

  /**
   * Handle when the server tells the client to start the game
   */
  public void start() {
    Optional<ClientService> clientService = commClient.getConnection();
    if(clientService.isPresent()) {
      clientService.get().send(new StartGameMessage());
      logger.debug("Ready to start! Waiting for the board");      
    } else {
      logger.fatal("ClientService doesn't exists!");
    }
  }


  public void rocketMove(UUID playerId) throws ConnectException {
    JoinScreen.RocketAnimation animation = ((JoinScreen) game.getScreen()).getMainPlayerAnimation();

    Optional<ClientService> clientService = commClient.getConnection();
    if(clientService.isPresent()) {
      clientService.get().send(new JoinScreenMoveMessage(playerId, animation.getPos(), animation.getRotation()));  
    } else {
      logger.fatal("ClientService doesn't exists!");
    }
  }

  public void updateRocket(JoinScreenMoveMessage data) {
    store.getJoinScreenStore().updateRocket(data.position, data.rotation, data.playerId);
  }

}
