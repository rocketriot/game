package bham.bioshock.minigame.objectives;

import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.communication.messages.UpdateObjectiveMessage.HealthMessage;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Entity.State;
import bham.bioshock.minigame.models.Gun;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Objective abstract class.
 */

public abstract class Objective implements Serializable, Cloneable {

  private static final long serialVersionUID = 7485771472370553399L;
  private static int INITIAL_HEALTH = 4;
  
  protected transient World world;
  protected transient Router router;
  protected transient MinigameStore localStore;
  private transient Position[] respawnPositions;
  protected HashMap<UUID, Integer> health = new HashMap<>();

  public abstract UUID getWinner();

  public void init(World world, Router router, MinigameStore store) {
    this.world = world;
    this.router = router;
    this.localStore = store;
    this.respawnPositions = world.getPlayerPositions();
    
    store.getPlayers().forEach(player -> {
      health.put(player.getId(), INITIAL_HEALTH);
    });
  }


  public Collection<Astronaut> getPlayers() {
    return localStore.getPlayers();
  }
  
  public int getHealth(UUID id) {
    return health.get(id);
  }
  
  public Set<Entry<UUID, Integer>> getHealthCopy() {
    return health.entrySet();
  }
  
  public void updateHealth(ArrayList<HealthMessage> health2) {
    for(HealthMessage h : health2) {
        health.put(h.id, h.value);
        updatePlayerHealth(h.id, h.value);
    }
  }

  protected abstract void updatePlayerHealth(UUID key, Integer value);

  /**
   * Called everytime a player is shot
   * 
   * @param player: the player who got shot
   * @param killer: the player who shot
   */
  public void gotShot(Astronaut player, Astronaut killer) {
    if(player.is(State.REMOVING)) return;
    health.computeIfPresent(player.getId(), (k, v) -> v - 1);
  }
  
  public boolean isDead(UUID playerId) {
    Integer h = health.get(playerId);
    return h != null && h <= 0;
  }
  
  protected Position getRandomRespawn() {
    Random r = new Random();
    int i = Math.abs(r.nextInt()%4);
    return respawnPositions[i];
  }

  
  protected void killAndRespawnPlayer(Astronaut player, Position randomRespawn) {
    boolean hadGun = player.haveGun();
    if(player.is(State.REMOVING)) return;
    player.killAndRespawn(randomRespawn);

    if (hadGun) {
      Gun gun = new Gun(world, player.getX(), player.getY());
      gun.load();
      localStore.addEntity(gun);
    }
    health.put(player.getId(), INITIAL_HEALTH);
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


}
