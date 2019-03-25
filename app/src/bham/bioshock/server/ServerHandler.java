package bham.bioshock.server;

import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.utils.Clock;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.interfaces.ServerService;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.boardgame.AddBlackHoleMessage;
import bham.bioshock.communication.messages.joinscreen.JoinScreenMoveMessage;
import bham.bioshock.communication.messages.minigame.RequestMinigameStartMessage;
import bham.bioshock.server.handlers.GameBoardHandler;
import bham.bioshock.server.handlers.JoinScreenHandler;
import bham.bioshock.server.handlers.MinigameHandler;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;
import bham.bioshock.server.interfaces.StoppableServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHandler implements MultipleConnectionsHandler {

  private static final Logger logger = LogManager.getLogger(ServerHandler.class);

  private ArrayList<ServerService> connecting = new ArrayList<>();
  private ConcurrentHashMap<UUID, ServerService> connected = new ConcurrentHashMap<>();

  private StoppableServer server;
  private JoinScreenHandler joinHandler;
  private GameBoardHandler gameBoardHandler;
  private MinigameHandler minigameHandler;
  private DevServer devServer;
  
  public ServerHandler(Store store, StoppableServer server, boolean runDebugServer, Clock clock) {
    this.server = server;
    joinHandler = new JoinScreenHandler(store, this);
    minigameHandler = new MinigameHandler(store, this, clock);
    gameBoardHandler = new GameBoardHandler(store, this, minigameHandler);
    
    if(runDebugServer) {
      startDebugServer(store);
    }
  }
  
  private void startDebugServer(Store store) {
    try {
      devServer = new DevServer();
      devServer.addServices(store, connecting, connected);
    } catch(IOException e) {}
  }
  
  /**
   * Used by ConnectionMaker to register new service
   * 
   * @param service
   */
  public void add(ServerService service) {
    connecting.add(service);
  }

  /**
   * Registers new client as connected with provided ID
   * 
   * @param id
   * @param service
   */
  public void register(UUID id, String username, ServerService service) {
    service.saveId(id, username);
    connecting.remove(service);
    connected.put(id, service);
  }

  /**
   * Sends the message to all clients
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
    ServerService service = connected.get(clientId);
    if(service != null) {
      connected.get(clientId).send(message);      
    }
  }

  /**
   * Register new player
   * 
   * @param action
   * @param service
   * @throws InvalidMessageSequence 
   */
  private void registerPlayer(Message message, ServerService service) throws InvalidMessageSequence {
    boolean knownService = connecting.contains(service);
    if (message.command.equals(Command.REGISTER) && knownService) {
      
      // Register new connected player
      joinHandler.registerPlayer(message, service);
    
    } else if(!knownService) {
      throw new InvalidMessageSequence("Unknown service, not in the list!");
    } else {
      throw new InvalidMessageSequence("Player must be registered first!");
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
   * Stop all running sub-services
   */
  public void abort() {
    for (ServerService s : connected.values()) {
      s.abort();
    }
    for (ServerService s : connecting) {
      s.abort();
    }
  }
  
  /**
   * Handle received message from one of the clients
   * 
   * @param action
   * @param service
   * @throws InvalidMessageSequence 
   */
  public void handleRequest(Message message, ServerService service) throws InvalidMessageSequence {
    logger.trace("Server received: " + message);

    // If the service doesn't have assigned Id register it
    if (!service.Id().isPresent()) {
      registerPlayer(message, service);        
      return;
    }
    if(connected.get(service.Id().get()) == null) {
      throw new InvalidMessageSequence("Unknown service, not in the list!");
    }

    UUID clientId = service.Id().get();

    switch (message.command) {
      case JOIN_SCREEN_MOVE:
        joinHandler.moveRocket((JoinScreenMoveMessage) message, clientId);
        break;
      case START_GAME:
        server.stopDiscovery();
        joinHandler.startGame(gameBoardHandler);
        break;
      case MOVE_PLAYER_ON_BOARD:
        gameBoardHandler.movePlayer(message, clientId);
        break;
      case END_TURN:
        gameBoardHandler.endTurn();
        break;
      case ADD_BLACK_HOLE:
        AddBlackHoleMessage addBlackHoleMessage = (AddBlackHoleMessage) message;
        gameBoardHandler.addBlackHole(addBlackHoleMessage.coordinates);
        break;
      case MINIGAME_START:
        RequestMinigameStartMessage request = (RequestMinigameStartMessage) message;
        minigameHandler.startMinigame(clientId, request.planetId, gameBoardHandler, request.objectiveId);
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
