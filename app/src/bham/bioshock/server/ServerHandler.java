package bham.bioshock.server;

import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.minigame.RequestMinigameStartMessage;
import bham.bioshock.communication.server.ServerService;
import bham.bioshock.server.handlers.GameBoardHandler;
import bham.bioshock.server.handlers.JoinScreenHandler;
import bham.bioshock.server.handlers.MinigameHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHandler {

  private static final Logger logger = LogManager.getLogger(ServerHandler.class);

  private ArrayList<ServerService> connecting = new ArrayList<>();
  private ConcurrentHashMap<UUID, ServerService> connected = new ConcurrentHashMap<>();

  private Server server;
  private JoinScreenHandler joinHandler;
  private GameBoardHandler gameBoardHandler;
  private MinigameHandler minigameHandler;
  private DevServer devServer;
  
  private final boolean DEBUG_SERVER = false;
  
  
  public ServerHandler(Store store, Server server) {
    this.server = server;
    joinHandler = new JoinScreenHandler(store, this);
    minigameHandler = new MinigameHandler(store, this);
    gameBoardHandler = new GameBoardHandler(store, this, minigameHandler);
    if(DEBUG_SERVER) {
      try {
        devServer = new DevServer();
        devServer.addServices(store, connecting, connected);
      } catch(IOException e) {}
    }
  }
  
  /**
   * Used by ConnectionMaker to register new service
   * 
   * @param service
   */
  public void register(ServerService service) {
    connecting.add(service);
  }

  /**
   * Registers new client as connected with provided ID
   * 
   * @param id
   * @param service
   */
  public void registerClient(UUID id, ServerService service) {
    service.saveId(id);
    connecting.remove(service);
    connected.put(id, service);
  }

  /**
   * Sends the message to all clients except the one specified
   * 
   * @param clientId
   * @param action
   */
  public void sendToAll(Message message) {
    for (ServerService s : connected.values()) {
      s.send(message);
    }
  }

  /**
   * Sends the message to all clients except the one specified
   * 
   * @param clientId
   * @param action
   */
  public void sendToAllExcept(Message message, UUID id) {
    for (ServerService s : connected.values()) {
      if (!s.Id().get().equals(id)) {
        s.send(message);
      }
    }
  }

  /**
   * Sends the message to the specific client
   * 
   * @param clientId
   * @param action
   */
  public void sendTo(UUID clientId, Message message) {
    connected.get(clientId).send(message);
  }

  /**
   * Register new player
   * 
   * @param action
   * @param service
   */
  private void registerPlayer(Message message, ServerService service) {
    if (message.command.equals(Command.REGISTER)) {
      joinHandler.registerPlayer(message, service);
    } else {
      logger.fatal("Invalid command sequence. Player must be registered first!");
    }
  }

  /**
   * Unregister player, remove the connection and send information to all clients
   * @param serverService
   */
  public void unregister(ServerService serverService) {
    Optional<UUID> clientId = serverService.Id();
    if(clientId.isPresent()) {
      connected.remove(clientId.get());
      joinHandler.disconnectPlayer(clientId.get());      
    } else {
      connecting.remove(serverService);      
    }
  }
  
  /**
   * Stop all running subservices
   */
  public void stopAll() {
    for (ServerService s : connected.values()) {
      s.abort();
    }
    for (ServerService s : connecting) {
      s.abort();
    }
  }
  
  /**
   * Handle received action
   * 
   * @param action
   * @param service
   */
  public void handleRequest(Message message, ServerService service) {
    logger.trace("Server received: " + message);

    // If the service doesn't have assigned Id register it
    if (!service.Id().isPresent()) {
      registerPlayer(message, service);
      return;
    }

    UUID clientId = service.Id().get();

    switch (message.command) {
      case JOIN_SCREEN_MOVE:
        joinHandler.moveRocket(message, clientId);
        break;
      case START_GAME:
        server.stopDiscovery();
        joinHandler.startGame(gameBoardHandler);
        break;
      case MOVE_PLAYER_ON_BOARD:
        gameBoardHandler.movePlayer(message, clientId);
        break;
      case END_TURN:
        gameBoardHandler.endTurn(clientId);
        break;
      case MINIGAME_START:
        RequestMinigameStartMessage data = (RequestMinigameStartMessage) message;
        minigameHandler.startMinigame(data, clientId, gameBoardHandler);
        break;
      case MINIGAME_PLAYER_MOVE:
        minigameHandler.playerMove(message, clientId);
        break;
      case MINIGAME_PLAYER_STEP:
        minigameHandler.playerStep(message, clientId);
        break;
      case MINIGAME_BULLET:
        minigameHandler.bulletShot(message, clientId);
        break;
      case MINIGAME_OBJECTIVE:
        minigameHandler.updateObjective(message);
        break;
      case MINIGAME_DIRECT_START:
        server.stopDiscovery();
        joinHandler.minigameDirectStart(clientId, gameBoardHandler, minigameHandler);
        break;
      default:
        logger.error("Received unhandled command: " + message.command.toString());
        break;
    }
  }

}
