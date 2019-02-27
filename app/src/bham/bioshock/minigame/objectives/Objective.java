package bham.bioshock.minigame.objectives;

import bham.bioshock.minigame.models.Player;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Objective {
  private ArrayList<Player> players;
  private Player mainPlayer;
  private HashMap<Player, Float> health;

  public Objective(ArrayList<Player> players, Player mainPlayer) {
    this.players = players;
    this.mainPlayer = mainPlayer;
    players.add(mainPlayer);
  }

  public void initialiseHealth(float initialHealth) {
    players.forEach(player -> health.put(player, initialHealth));
  }

  public void setHealth(float health, Player player) {
    this.health.replace(player, health);
  }

  public void isShot(Player shooter, Player shot) {
    setHealth(health.get(shot) -3, shot);

    if(health.get(shot) <=0)
      playerDead (shooter,shot);
  }

  public abstract void playerDead(Player killer, Player dead);

  public abstract Player getWinner();
}