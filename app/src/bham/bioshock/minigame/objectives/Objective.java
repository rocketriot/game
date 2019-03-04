package bham.bioshock.minigame.objectives;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Astronaut;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class Objective {
  private Collection<Astronaut> players;
  private Astronaut mainPlayer;
  private float initialHealth = 100;

  public Objective(Collection<Astronaut> players, Astronaut mainPlayer) {
    this.players = players;
    this.mainPlayer = mainPlayer;
    initialiseHealth();
  }

  public abstract Astronaut getWinner();
  public abstract void handleDead();

  public void initialiseHealth() {
    players.forEach(player -> player.setHealth(initialHealth));
  }
  public Collection<Astronaut> getPlayers(){return this.players;}
  public float getInitialHealth(){return this.initialHealth;}

}