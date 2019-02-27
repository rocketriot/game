package bham.bioshock.minigame.objectives;
import bham.bioshock.minigame.models.Player;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Objective {
  private ArrayList<Player> players;
  private Player mainPlayer;

  public Objective(ArrayList<Player> players, Player mainPlayer) {
    this.players = players;
    this.mainPlayer = mainPlayer;
    players.add(mainPlayer);
  }

  public void initialiseHealth(float initialHealth) {
    players.forEach(player -> player.setHealth(initialHealth));
  }



  public abstract Player getWinner();
}