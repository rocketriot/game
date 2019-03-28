package bham.bioshock.minigame.ai;

import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.minigame.BulletShotMessage;
import bham.bioshock.communication.messages.minigame.MinigamePlayerMoveMessage;
import bham.bioshock.communication.messages.minigame.MinigamePlayerStepMessage;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.models.astronaut.AstronautMove;
import bham.bioshock.minigame.objectives.Platformer;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;

import java.util.UUID;

/** The type Minigame ai. */
public abstract class MinigameAI {

  /** The Store. */
  protected Store store;
  /** The Id. */
  protected UUID id;
  /** The Astronaut. */
  protected CpuAstronaut astronaut;
  /** The Local store. */
  protected MinigameStore localStore;
  /** The Server Handler. */
  private MultipleConnectionsHandler handler;

  /**
   * Instantiates a new Minigame ai.
   *
   * @param id the id of the ai
   * @param store the store
   * @param handler the handler
   */
  public MinigameAI(UUID id, Store store, MultipleConnectionsHandler handler) {
    this.store = store;
    this.handler = handler;
    this.id = id;
  }

  /**
   * Run the ai.
   *
   * @param delta the time between iterations
   */
  public final void run(float delta) {
    if (localStore == null) {
      localStore = store.getMinigameStore();
      if (localStore == null) return;
    }
    if (astronaut == null) {
      astronaut = new CpuAstronaut(localStore.getPlayer(id), localStore.getWorld());
      if (astronaut == null) return;
    }

    update(delta);
    afterUpdate();
  }

  /**
   * Update after the AI changes.
   *
   * @param delta the time between iterations
   */
  public abstract void update(float delta);

  /** After update. */
  public void afterUpdate() {
    if (localStore.getObjective() instanceof Platformer) {
      Platformer o = (Platformer) store.getMinigameStore().getObjective();
      if (o.checkIfFrozen(astronaut.get().getId())) {
        handler.sendToAll(new MinigamePlayerMoveMessage(astronaut, new AstronautMove()));
        return;
      }
    }

    AstronautMove move = astronaut.endMove();

    // Send to all new move, position and speed vector
    if (move != null) {
      handler.sendToAll(new MinigamePlayerMoveMessage(astronaut, move));
    }
    handler.sendToAll(new MinigamePlayerStepMessage(astronaut.get()));

    for (Bullet b : astronaut.getBullets()) {
      handler.sendToAll(new BulletShotMessage(b));
    }
    astronaut.clearBullets();
  }
}
