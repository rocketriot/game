package bham.bioshock.minigame.objectives;

import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.worlds.World;
import java.io.Serializable;
import java.util.Collection;

/**
 * Objective abstract class.
 */

public abstract class Objective implements Serializable {

  private World world;
  private Router router;
  protected MinigameStore localSore;

  public Objective(World world) {
    this.world = world;
  }

  private Collection<Astronaut> players;

  public abstract Astronaut getWinner();

  public void setPlayers(Collection<Astronaut> players) {
    this.players = players;
    initialise();
  }

  public void setRouter(Router router){this.router = router;}

  public Collection<Astronaut> getPlayers() {
    return this.players;
  }

  public World getWorld(){return this.world;}

  public Router getRouter(){return this.router;}

  /**
   * Called everytime a player is shot
   * @param player: the player who got shot
   * @param killer: the player who shot
   */
  public abstract void gotShot(Astronaut player, Astronaut killer);

  public abstract void initialise();

  /**
   * Seeds the minigame store with the additional entities required to each objective
   * @param store
   */
  public abstract void seed(MinigameStore store);

  /**
   * Called when the flag is captured
   * @param a: the player who got the flag
   */
  public abstract void captured(Astronaut a);

  /**
   * The instructions of each objective
   * @return the instruction String
   */
  public abstract String instructions();
}
