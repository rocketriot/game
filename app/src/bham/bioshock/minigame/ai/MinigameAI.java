package bham.bioshock.minigame.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;
import bham.bioshock.minigame.models.Astronaut;

abstract public class MinigameAI {

  private ServerHandler handler;
  protected Store store;
  protected UUID id;
  protected Astronaut astronaut;
  
  public MinigameAI(UUID id, Store store, ServerHandler handler) {
    this.store = store;
    this.handler = handler;
    this.id = id;
  }
  
  public final void run(float delta) {
    MinigameStore localStore = store.getMinigameStore();
    if(localStore == null) return;
    if(astronaut == null) {
      astronaut = localStore.getPlayer(id);
      if(astronaut == null) return;
    }
    
    update(delta);
    afterUpdate();
  }

  abstract public void update(float delta);

  public void afterUpdate() {
    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add((Serializable) id);
    arguments.add((Serializable) astronaut.getSpeedVector());
    arguments.add((Serializable) astronaut.getPos());
    arguments.add((Serializable) astronaut.getDirection());
    arguments.add((Serializable) astronaut.haveGun());
    
    handler.sendToAll(new Action(Command.MINIGAME_PLAYER_MOVE, arguments));
  }
}
