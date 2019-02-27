package bham.bioshock.minigame.objectives;

import bham.bioshock.minigame.models.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class KillThemAll extends Objective {
  private HashMap<Player, Integer> kills = new HashMap<>();

  public KillThemAll(ArrayList<Player> players, Player mainPlayer){
    super(players, mainPlayer);
  }

  public void setKills(Player killer, int value){
    kills.replace(killer, value);
  }
  @Override
  public void playerDead(Player killer, Player dead) {
    setKills(killer, kills.get(killer)+1);

    // after a player dies it is moved
  }

  @Override
  public Player getWinner() {
    Player winner  = Collections.max(kills.entrySet(), Map.Entry.comparingByValue()).getKey();

    return winner;
  }


}
