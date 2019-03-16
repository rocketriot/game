package bham.bioshock.minigame.objectives;

import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.worlds.World;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 * Objective abstract class.
 */

public abstract class Objective implements Serializable {

  protected transient World world;
  protected transient Router router;
  protected transient MinigameStore localStore;

  public abstract UUID getWinner();

  public void init(World world, Router router, MinigameStore store) {
    this.world = world;
    this.router = router;
    this.localStore = store;
  }


  public Collection<Astronaut> getPlayers() {
    return localStore.getPlayers();
  }

  /**
   * Called everytime a player is shot
   * 
   * @param player: the player who got shot
   * @param killer: the player who shot
   */
  public abstract void gotShot(Astronaut player, Astronaut killer);


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
