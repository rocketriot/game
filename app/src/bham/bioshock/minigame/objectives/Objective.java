package bham.bioshock.minigame.objectives;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.worlds.World;

import java.util.Collection;


public abstract class Objective {

  private World world;

  public Objective(World world){
    this.world = world;
  }
  private Collection<Player> players;
  public abstract Player getWinner();

  public void setPlayers(Collection<Player> players){
    this.players = players;
    initialise();
  }
  public Collection<Player> getPlayers(){return this.players;}
  public abstract void gotShot(Player player, Player killer);
  public abstract void initialise();
  public World getWorld(){return this.world;}
  public abstract void seed(MinigameStore store);

}