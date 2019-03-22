package bham.bioshock.minigame.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.server.ServerHandler;
import bham.bioshock.minigame.models.Bullet;

/** The type Minigame ai. */
public abstract class MinigameAI {

  /** The Server Handler. */
  private ServerHandler handler;

  /** The Store. */
  protected Store store;

  /** The Id. */
  protected UUID id;

  /** The Astronaut. */
  protected CpuAstronaut astronaut;

  /** The Local store. */
  protected MinigameStore localStore;

  /**
   * Instantiates a new Minigame ai.
   *
   * @param id the id of the ai
   * @param store the store
   * @param handler the handler
   */
  public MinigameAI(UUID id, Store store, ServerHandler handler) {
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
    if(localStore == null) {
      localStore = store.getMinigameStore();
      if(localStore == null) return;
    };
    if(astronaut == null) {
      astronaut = new CpuAstronaut(localStore.getPlayer(id), localStore.getWorld());
      if(astronaut == null) return;
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
    astronaut.moveChange();

    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add(id);
    arguments.add(astronaut.get().getSpeedVector());
    arguments.add(astronaut.get().getPos());
    arguments.add(astronaut.get().getMove());
    arguments.add(astronaut.get().haveGun());

    // Send to all except the host
    handler.sendToAllExcept(
        new Action(Command.MINIGAME_PLAYER_MOVE, arguments),
        store.getMainPlayer().getId()
    );

    Collection<Bullet> bullets = astronaut.getBullets();

    for(Bullet b : bullets) {
      ArrayList<Serializable> args = new ArrayList<>();
      args.add(b.getSpeedVector());
      args.add(b.getPos());
      b.load();

      handler.sendToAll(
          new Action(Command.MINIGAME_BULLET, args)
      );
    }
    astronaut.clearBullets();
  }
}
