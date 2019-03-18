package bham.bioshock.server;

import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.server.ServerService;
import bham.bioshock.server.handlers.GameBoardHandler;
import bham.bioshock.server.handlers.JoinScreenHandler;
import bham.bioshock.server.handlers.MinigameHandler;
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

  public ServerHandler(Store store, Server server) {
    this.server = server;
    joinHandler = new JoinScreenHandler(store, this);
    minigameHandler = new MinigameHandler(store, this);
    gameBoardHandler = new GameBoardHandler(store, this, minigameHandler);
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
   * Sends the action to all clients except the one specified
   * 
   * @param clientId
   * @param action
   */
  public void sendToAll(Action action) {
    for (ServerService s : connected.values()) {
      s.send(action);
    }
  }
  
  /**
  * Sends the action to all clients except the one specified
  * 
  * @param clientId
  * @param action
  */
 public void sendToAll(Message m) {
   sendToAll(Action.of(m));
 }

  /**
   * Sends the action to all clients except the one specified
   * 
   * @param clientId
   * @param action
   */
  public void sendToAllExcept(Action action, UUID id) {
    for (ServerService s : connected.values()) {
      if (!s.Id().get().equals(id)) {
        s.send(action);
      }
    }
  }

  /**
   * Sends the action to the specific client
   * 
   * @param clientId
   * @param action
   */
  public void sendTo(UUID clientId, Action action) {
    connected.get(clientId).send(action);
  }


  /**
   * Register new player
   * 
   * @param action
   * @param service
   */
  private void registerPlayer(Action action, ServerService service) {
    if (action.getCommand().equals(Command.REGISTER)) {
      joinHandler.registerPlayer(action, service);
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
  public void handleRequest(Action action, ServerService service) {
    logger.trace("Server received: " + action);

    // If the service doesn't have assigned Id register it
    if (!service.Id().isPresent()) {
      registerPlayer(action, service);
      return;
    }

    UUID clientId = service.Id().get();

    switch (action.getCommand()) {
      case JOIN_SCREEN_MOVE:
        joinHandler.moveRocket(action, clientId);
        break;
      case START_GAME:
        server.stopDiscovery();
        joinHandler.startGame(action, gameBoardHandler);
        break;
      case MOVE_PLAYER_ON_BOARD:
        gameBoardHandler.movePlayer(action, clientId);
        break;
      case MINIGAME_START:
        minigameHandler.startMinigame(action, clientId, gameBoardHandler);
        break;
      case MINIGAME_PLAYER_MOVE:
        minigameHandler.playerMove(action, clientId);
        break;
      case MINIGAME_BULLET:
        minigameHandler.bulletShot(action, clientId);
        break;
      case END_TURN:
        gameBoardHandler.endTurn((UUID) action.getArgument(0));
        break;
      case MINIGAME_DIRECT_START:
        server.stopDiscovery();
        joinHandler.minigameDirectStart(action, clientId, gameBoardHandler, minigameHandler);
        break;
      default:
        logger.error("Received unhandled command: " + action.getCommand().toString());
        break;
    }
  }

}
