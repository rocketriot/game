package bham.bioshock.minigame.objectives;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Player;

import java.util.Collection;
import java.util.HashMap;


public class KillThemAll extends Objective {
  private Position respawnPosition = new Position(0, 0);
  private HashMap<Player, Float> health = new HashMap<>();
  private HashMap<Player, Integer> kills = new HashMap<>();
  private float initialHealth = 100.0f;

  public KillThemAll(){

  }

  @Override
  public Player getWinner() {
    return null;
  }


  @Override
  public void gotShot(Player player, Player killer) {
    System.out.println("xxxxx");
    addKill(killer);
    setPlayerHealth(initialHealth, player);
    player.setPosition(new Position(0,0));
  }

  @Override
  public void initialise() {
    getPlayers().forEach(player -> {
      health.put(player,initialHealth);
      kills.put(player,0);
    });
  }


  public float getPlayerHealth(Player p){
    return health.get(p);
  }

  public void setPlayerHealth(float newHealth, Player p){
    health.computeIfPresent(p, (k, v) -> newHealth);
  }

  public float getPlayerKills(Player p){ return kills.get(p); }

  public void addKill( Player p){ health.computeIfPresent(p, (k, v) -> (v+1)); }

}
