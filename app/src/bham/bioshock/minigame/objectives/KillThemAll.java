package bham.bioshock.minigame.objectives;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Player;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class KillThemAll extends Objective {
  private Position respawnPosition = new Position(-2300, 0);
  private HashMap<Player, Float> health = new HashMap<>();
  private HashMap<Player, Integer> kills = new HashMap<>();
  private float initialHealth = 10.0f;

  @Override
  public Player getWinner() {
    return Collections.max(kills.entrySet(), Comparator.comparingInt(HashMap.Entry::getValue)).getKey();
  }


  @Override
  public void gotShot(Player player, Player killer) {
    if(checkIfdead(player)) {
      addKill(killer);
      player.setPosition(respawnPosition);
      setPlayerHealth(initialHealth, player);
    } else {
      float newHealth = health.get(player) - 5.0f;
      setPlayerHealth(newHealth, player);
    }
  }

  @Override
  public void initialise() {
    getPlayers().forEach(player -> {
      health.put(player,initialHealth);
      kills.put(player,0);
    });
  }


  //test
  public void addPlayer(Player p){
    health.put(p,initialHealth);
    kills.put(p,0);
  }

  public boolean checkIfdead(Player p){
    if(health.get(p) - 5.0f <=0)
      return true;
    return false;
  }
  public void setPlayerHealth(float newHealth, Player p){
    health.computeIfPresent(p, (k, v) -> newHealth);
  }
  public void addKill( Player p){ kills.computeIfPresent(p, (k, v) -> (v+1)); }

}
