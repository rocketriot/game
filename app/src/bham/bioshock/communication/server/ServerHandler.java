package bham.bioshock.communication.server;

import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
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
  private Store store;
  private JoinScreenHandler joinHandler;
  private GameBoardHandler gameBoardHandler;
  private MinigameHandler minigameHandler;
  
  public ServerHandler() {
    connections = new ArrayList<>();
    store = new Store();
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
        if(s.Id() != id) {
          s.send(action);        
        }
      }
    }
  }
  
  public void sendTo(UUID clientId, Action action) {
    synchronized(connections) {
      for(ServerService s : connections) {
        if(s.Id() == clientId) {
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
        case START_GAME:
          joinHandler.startGame(action, gameBoardHandler);
          break;
        case GET_GAME_BOARD:
          gameBoardHandler.getGameBoard(action);
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
        case MINIGAME_END:
          minigameHandler.endMinigame(action, service.Id());
          break;
        case MINIGAME_BULLET:
          minigameHandler.bulletShot(action, service.Id());
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
