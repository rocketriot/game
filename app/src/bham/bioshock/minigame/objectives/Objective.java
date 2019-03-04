package bham.bioshock.minigame.objectives;
import bham.bioshock.minigame.models.Player;
import java.util.Collection;


public abstract class Objective {
  private Collection<Player> players;

  private Player mainPlayer;
  private float initialHealth = 100;

  public abstract Player getWinner();

  public void setPlayers(Collection<Player> players){
    this.players = players;
    initialise();
  }
  public Collection<Player> getPlayers(){return this.players;}
  public abstract void gotShot(Player player, Player killer);
  public abstract void initialise();

  // test
  public abstract void addPlayer(Player p);

}