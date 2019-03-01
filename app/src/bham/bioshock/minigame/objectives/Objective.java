package bham.bioshock.minigame.objectives;
import bham.bioshock.minigame.models.Player;
import java.util.Collection;


public abstract class Objective {
  private Collection<Player> players;
  public abstract Player getWinner();

  public void setPlayers(Collection<Player> players){this.players = players;}
  public Collection<Player> getPlayers(){return this.players;}

}