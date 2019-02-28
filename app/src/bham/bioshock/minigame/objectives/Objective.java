package bham.bioshock.minigame.objectives;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Player;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class Objective {
  private Collection<Player> players;
  private Player mainPlayer;
  private float initialHealth = 100;

  public Objective(Collection<Player> players, Player mainPlayer) {
    this.players = players;
    this.mainPlayer = mainPlayer;
    initialiseHealth();
    players.add(mainPlayer);
  }

  public abstract Player getWinner();
  public abstract void handleDead();

  public void initialiseHealth() {
    players.forEach(player -> player.setHealth(initialHealth));
  }
  public Collection<Player> getPlayers(){return this.players;}
  public float getInitialHealth(){return this.initialHealth;}

}