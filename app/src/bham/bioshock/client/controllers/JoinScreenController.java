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
import bham.bioshock.client.screens.RunningServersScreen;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.JoinScreenStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.client.CommunicationClient;
import bham.bioshock.communication.client.ServerStatus;
import bham.bioshock.communication.messages.boardgame.StartGameMessage;
import bham.bioshock.communication.messages.joinscreen.JoinScreenMoveMessage;
import bham.bioshock.communication.messages.joinscreen.ReconnectMessage;
import bham.bioshock.communication.messages.joinscreen.ReconnectResponseMessage;
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

  public void registerAndShow(String username) {
    // Create a new player
    Player player = new Player(username);

    // Save player to the store
    store.setMainPlayer(player.getId());
    Optional<ClientService> service = commClient.getConnection();
    
    if(service.isPresent()) {
      // Add the player to the server
      commClient.getConnection().get().send(new RegisterMessage(player));
      show(player);     
    } else {
      logger.fatal("ClientService doesn't exists, reconnect thread should try to reconnect");
    }
  }
  
  public void show(Player player) {
    store.setJoinScreenStore(new JoinScreenStore());
    // Create connection to the server
    setScreen(new JoinScreen(router, store, player));
  }
  
  public void connect(ServerStatus server) {
    try {
      connectToServer(server);
    } catch (ConnectException e) {
      // Server cannot be started
      logger.error(e.getMessage());
      router.call(Route.ALERT, e.getMessage());
    }
  }
  
  public void showServers() {
    commClient.discover(store.getCommStore(), router);
    setScreen(new RunningServersScreen(store.getCommStore(), router));
  }  
  
  public void reconnectRecovered() {
    ServerStatus server = store.getCommStore().getRecoveredServer();
    // Save player to the store
    store.setMainPlayer(server.getPlayerId());

    boolean successful = commClient.reconnect(server.getIP());
    if(successful) {
      router.call(Route.LOADING, new String("Reconnecting"));
      router.call(Route.SEND_RECONNECT);
    } else {
      router.call(Route.MAIN_MENU);
      // Server cannot be started
      logger.error("Can't reconnect");
      router.call(Route.ALERT, "Connection unsuccessful");        
    } 
  }
  
  public void disconnect() {
    commClient.disconnect();
    store.removeAllPlayers();
    router.back();
  }
  
  /**
   * For reconnection
   */
  public void updateReconnect(ReconnectResponseMessage data) {
    store.overwritePlayers(data.players);
    store.setTurn(data.turnNum);
    store.setRound(data.roundNum);
    
    if(data.boardgameRunning) {
      router.call(Route.COORDINATES_SAVE, data.coordinates);  
      router.call(Route.GAME_BOARD_SAVE, data.gameBoard); 
      router.call(Route.GAME_BOARD_SHOW);      
    } else {
      ArrayList<JoiningPlayer> players = new ArrayList<>();
      for(Player p : data.players) {
        players.add(new JoiningPlayer(p));
      }
      router.call(Route.ADD_PLAYER, players);
      show(store.getMainPlayer());
    }
  }
  
  public void sendReconnect() {
    Optional<ClientService> clientService = commClient.getConnection();
    if(clientService.isPresent()) {
      clientService.get().registerHandler(clientHandler);
      UUID playerId = store.getMainPlayerId();
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
  private void connectToServer(ServerStatus server) throws ConnectException {
    // Create server connection
    ClientService service = commClient.connect(server);
    service.registerHandler(clientHandler);
    commClient.startReconnectionThread(router);
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
