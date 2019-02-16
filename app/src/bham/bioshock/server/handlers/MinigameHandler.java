package bham.bioshock.server.handlers;

import java.util.UUID;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.server.ServerHandler;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.FirstWorld;

public class MinigameHandler {

  Store store;
  ServerHandler handler;
  
  public MinigameHandler (Store store, ServerHandler handler) {
    this.store = store;
    this.handler = handler;
  }
  
  /*
   * Create and seed the world, and send start game command to all clients
   */
  public void startMinigame(Action action) {
    MinigameStore miniGameStore = new MinigameStore();
    miniGameStore.seed(store, new FirstWorld() );
    
    store.setMinigameStore(miniGameStore);
    handler.sendToAll(action);
  }
  
  /*
   * Sync player movement and position
   */
  public void playerMove(Action action, UUID playerId) {
    MinigameStore localStore = store.getMinigameStore();
    SpeedVector speed = (SpeedVector) action.getArgument(1);
    Position pos = (Position) action.getArgument(2);
    
    localStore.updatePlayer(playerId, speed, pos, null);
    
    handler.sendToAllExcept(action, playerId);
  }
}
