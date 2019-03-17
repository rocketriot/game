package bham.bioshock.minigame.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Astronaut.Move;
import bham.bioshock.minigame.physics.Step;
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
    astronaut.moveChange();

    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add((Serializable) id);
    arguments.add((Serializable) astronaut.get().getSpeedVector());
    arguments.add((Serializable) astronaut.get().getPos());
    arguments.add((Serializable) astronaut.get().getMove());
    arguments.add((Serializable) astronaut.get().haveGun());
    
    // Send to all except the host
    handler.sendToAllExcept(
        new Action(Command.MINIGAME_PLAYER_MOVE, arguments), 
        store.getMainPlayer().getId()
    );
  }
}
