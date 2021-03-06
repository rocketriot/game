package bham.bioshock.minigame.objectives;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.objectives.*;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Entity.State;
import bham.bioshock.minigame.models.astronaut.Equipment;
import bham.bioshock.minigame.worlds.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/** Objective abstract class. */
public abstract class Objective implements Serializable {

  private static final Logger logger = LogManager.getLogger(Objective.class);
  private static final long serialVersionUID = 7485771472370553399L;
  private static int INITIAL_HEALTH = 6;

  protected transient Store store;
  protected transient World world;
  protected transient Router router;
  protected transient MinigameStore localStore;
  protected transient HashMap<UUID, Long> lastRespawn;
  protected HashMap<UUID, Integer> health = new HashMap<>();
  private transient Position[] respawnPositions;

  public abstract UUID getWinner();

  public abstract MinigameType getMinigameType();

  /**
   * Init the objective
   *
   * @param world
   * @param router
   * @param store
   */
  public void init(World world, Router router, Store store) {
    this.world = world;
    this.router = router;
    this.store = store;
    this.localStore = store.getMinigameStore();
    this.respawnPositions = world.getPlayerPositions();
    this.lastRespawn = new HashMap<>();

    store
        .getPlayers()
        .forEach(
            player -> {
              health.put(player.getId(), INITIAL_HEALTH);
            });
  }

  /**
   * Get player health
   *
   * @param id
   * @return 0-4 health of a player
   */
  public int getHealth(UUID id) {
    return health.get(id);
  }

  /**
   * Called every time a player is shot. Handled only by the host
   *
   * @param player: the player who got shot
   * @param killer: the player who shot
   */
  public void gotShot(Astronaut player, UUID killer) {
    if (player.is(State.REMOVING)) return;
    if (!store.isHost()) return;

    Integer h;
    synchronized (health) {
      h = health.get(player.getId());
    }
    if (h == null || h > 1) {
      // Decrease health request
      router.call(Route.SEND_OBJECTIVE_UPDATE, new UpdateHealthMessage(player.getId(), killer));
    } else {
      // Send kill and update request
      router.call(
          Route.SEND_OBJECTIVE_UPDATE,
          new KillAndRespawnMessage(player.getId(), killer, getRandomRespawn()));
    }
  }

  public final void pickupHeart(Astronaut player, UUID heartID) {
    if (player.is(State.REMOVING)) return;
    if (!store.isHost()) return;
    Entity entity = store.getMinigameStore().getEntity(heartID);
    if (entity == null) return;
    if (entity.isRemoved()) return;

    router.call(Route.SEND_OBJECTIVE_UPDATE, new IncreaseHealthMessage(player.getId(), heartID));
  }

  /**
   * Default handler for UpdateHealthMessage - just decrease health check with last respawn time to
   * avoid decreasing health after respawn
   *
   * @param m the message
   */
  public void handle(UpdateHealthMessage m) {
    if (lastRespawn.get(m.playerId) == null || lastRespawn.get(m.playerId) < m.created) {
      Astronaut astro = localStore.getPlayer(m.playerId);

      Equipment equipment = astro.getEquipment();
      if (equipment.haveShield) {
        equipment.removeShieldHealth();
      } else {
        health.computeIfPresent(m.playerId, (k, v) -> v - 1);
      }
    }
  }

  /**
   * Default handler for IncreaseHealthMessage - just increase health
   *
   * @param m the message
   */
  public void handle(IncreaseHealthMessage m) {
    synchronized (health) {
      Integer hp = health.get(m.playerID);
      if (hp != null && hp < INITIAL_HEALTH) {
        health.computeIfPresent(m.playerID, (k, v) -> v + 1);

        if (m.playerID == store.getMainPlayerId()) {
          SoundController.playSound("health");
        }

        Collection<Entity> entities = localStore.getEntities();
        for (Entity entity : entities) {
          if (entity.getId().equals(m.heartID)) {
            entity.remove();
          }
        }
      }
    }
  }

  /**
   * Default handler for KillAndRespawnMessage - kill player and respawn in the new position save
   * respawn position to ignore all bullets created before that moment
   *
   * @param m
   */
  public void handle(KillAndRespawnMessage m) {
    lastRespawn.put(m.playerId, m.created);
    killAndRespawnPlayer(m.playerId, m.position);
  }

  /**
   * Generates random respawn position based on the 4 initial positions
   *
   * @return respawn position
   */
  protected Position getRandomRespawn() {
    Random r = new Random();
    int i = Math.abs(r.nextInt() % 4);
    return respawnPositions[i];
  }

  /**
   * Handles player kill and respawn in provided position
   *
   * @param playerId
   * @param randomRespawn
   */
  protected void killAndRespawnPlayer(UUID playerId, Position randomRespawn) {
    Astronaut player = localStore.getPlayer(playerId);
    player.killAndRespawn(randomRespawn);
    synchronized (health) {
      health.put(player.getId(), INITIAL_HEALTH);
    }
  }

  /**
   * Handle different update messages
   *
   * @param m
   */
  public void handleMessage(Message m) {
    if (m instanceof UpdateHealthMessage) {
      this.handle((UpdateHealthMessage) m);
    } else if (m instanceof KillAndRespawnMessage) {
      this.handle((KillAndRespawnMessage) m);
    } else if (m instanceof FlagOwnerUpdateMessage) {
      this.handle((FlagOwnerUpdateMessage) m);
    } else if (m instanceof IncreaseHealthMessage) {
      this.handle((IncreaseHealthMessage) m);
    } else if (m instanceof UpdateFrozenMessage) {
      this.handle((UpdateFrozenMessage) m);
    }
  }

  /**
   * Unhandled messages
   *
   * @param m
   */
  public void handle(Message m) {
    logger.debug("Ignored message: " + m.getClass().getSimpleName());
  }

  public void handle(FlagOwnerUpdateMessage m) {
    logger.debug("Ignored message: " + m.getClass().getSimpleName());
  }

  public void handle(UpdateFrozenMessage m) {
    logger.debug("unhandled update frozen message");
  }

  /**
   * Seeds the minigame store with the additional entities required to each objective
   *
   * @param store
   */
  public abstract void seed(MinigameStore store);

  /**
   * Called when the flag is captured
   *
   * @param a: the player who got the flag
   */
  public abstract void captured(Astronaut a);

  /**
   * The instructions of each objective
   *
   * @return the instruction String
   */
  public abstract String instructions();

  /**
   * The name of each objective
   *
   * @return the name String
   */
  public abstract String name();

  public static enum MinigameType {
    CAPTURE_THE_FLAG,
    KILL_THEM_ALL,
    PLATFORMER
  }
}
