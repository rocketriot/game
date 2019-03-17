package bham.bioshock.communication.server;

import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.server.Server;
import bham.bioshock.server.handlers.GameBoardHandler;
import bham.bioshock.server.handlers.JoinScreenHandler;
import bham.bioshock.server.handlers.MinigameHandler;
import java.util.ArrayList;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHandler {
  
  private static final Logger logger = LogManager.getLogger(ServerHandler.class);

  private ArrayList<ServerService> connections;
  private Server server;
  private JoinScreenHandler joinHandler;
  private GameBoardHandler gameBoardHandler;
  private MinigameHandler minigameHandler;
  
  public ServerHandler(Store store, Server server) {
    connections = new ArrayList<>();
    this.server = server;
    joinHandler = new JoinScreenHandler(store, this);
    gameBoardHandler = new GameBoardHandler(store, this);
    minigameHandler = new MinigameHandler(store, this);
  }

  public void register(ServerService service) {
    connections.add(service);
  }

  public ArrayList<ServerService> getConnections() {
    return connections;
  }

  public void sendToAll(Action action) {
    synchronized(connections) {
      for (ServerService s : connections) {
        s.send(action);
      }
    }
  }

  public void sendToAllExcept(Action action, UUID id) {
    synchronized(connections) {
      for(ServerService s : connections) {
        if(!s.Id().equals(id)) {
          s.send(action);        
        }
      }
    }
  }
  
  public void sendTo(UUID clientId, Action action) {
    synchronized(connections) {
      for(ServerService s : connections) {
        if(s.Id().equals(clientId)) {
          s.send(action);
          return;
        }
      }
    }
  }



  public void handleRequest(Action action, ServerService service) {
    logger.trace("Server received: " + action);
    
    try {
      switch (action.getCommand()) {
        case REMOVE_PLAYER:
          joinHandler.disconnectPlayer(service);
          break;
        case ADD_PLAYER:
          joinHandler.addPlayer(action, service);
          break;
        case JOIN_SCREEN_MOVE:
          joinHandler.moveRocket(action, service.Id());
          break;
        case START_GAME:
          server.stopDiscovery();
          joinHandler.startGame(action, gameBoardHandler);
          break;
        case GET_GAME_BOARD:
          gameBoardHandler.getGameBoard(action, null);
          break;
        case MOVE_PLAYER_ON_BOARD:
          gameBoardHandler.movePlayer(action);
          break;
        case MINIGAME_START:
          minigameHandler.startMinigame(action);
          break;
        case MINIGAME_PLAYER_MOVE:
          minigameHandler.playerMove(action, service.Id());
          break;
        case MINIGAME_BULLET:
          minigameHandler.bulletShot(action, service.Id());
          break;
        case END_TURN:
          gameBoardHandler.endTurn((UUID) action.getArgument(0));
          break;
        case MINIGAME_DIRECT_START:
          joinHandler.minigameDirectStart(action, gameBoardHandler, minigameHandler);
          break;
        default:
          logger.error("Received unhandled command: " + action.getCommand().toString());
          break;
      }
    
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
