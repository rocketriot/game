package bham.bioshock.minigame.ai;

import java.util.UUID;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.MinigamePlayerMoveMessage;
import bham.bioshock.communication.messages.MinigamePlayerStepMessage;
import bham.bioshock.minigame.models.Astronaut.Move;
import bham.bioshock.server.ServerHandler;

abstract public class MinigameAI {

  private ServerHandler handler;
  protected Store store;
  protected UUID id;
  protected CpuAstronaut astronaut;
  protected MinigameStore localStore;
  
  public MinigameAI(UUID id, Store store, ServerHandler handler) {
    this.store = store;
    this.handler = handler;
    this.id = id;
  }
  
  public final void run(float delta) {
    if(localStore == null) {
      localStore = store.getMinigameStore();
      if(localStore == null) return;
    };
    if(astronaut == null) {
      astronaut = new CpuAstronaut(localStore.getPlayer(id));
      if(astronaut == null) return;
    }
    
    update(delta);
    afterUpdate();
  }

  abstract public void update(float delta);

  public void afterUpdate() {
    Move move = astronaut.endMove();

    handler.sendToAll(new MinigamePlayerMoveMessage(astronaut, move));
    // Send to all except the host
    handler.sendToAll(new MinigamePlayerStepMessage(astronaut.get()));
  }
}
