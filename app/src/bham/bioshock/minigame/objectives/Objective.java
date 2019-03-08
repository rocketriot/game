package bham.bioshock.minigame.objectives;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.worlds.World;

import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.worlds.World;

import java.io.Serializable;
import java.util.Collection;


public abstract class Objective implements Serializable {

  private World world;

  public Objective(World world) {
    this.world = world;
  }

  private Collection<Astronaut> players;

  public abstract Astronaut getWinner();

  public void setPlayers(Collection<Astronaut> players) {
    this.players = players;
    initialise();
  }

  public Collection<Astronaut> getPlayers() {
    return this.players;
  }

  public abstract void gotShot(Astronaut player, Astronaut killer);

  public abstract void initialise();

  public World getWorld(){return this.world;}
  public abstract void seed(MinigameStore store);

  public abstract void captured(Astronaut a);


}
