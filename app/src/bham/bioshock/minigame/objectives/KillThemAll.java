package bham.bioshock.minigame.objectives;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.worlds.World;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;


public class KillThemAll extends Objective {
  private Position respawnPosition;
  private HashMap<Player, Float> health = new HashMap<>();
  private HashMap<Player, Integer> kills = new HashMap<>();
  private float initialHealth = 100.0f;
  private Position[] positions;

  public KillThemAll(World world) {
    super(world);
    this.positions = this.getWorld().getPlayerPositions();
    setRandonRespawnPosition();
  }

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


  private boolean checkIfdead(Player p){
    if(health.get(p) - 5.0f <=0)
      return true;
    return false;
  }
  private void setPlayerHealth(float newHealth, Player p){
    health.computeIfPresent(p, (k, v) -> newHealth);
  }
  private void addKill( Player p){ kills.computeIfPresent(p, (k, v) -> (v+1)); }

  private void setRandonRespawnPosition(){
    Random r = new Random();
    int i = r.nextInt()%4;
    respawnPosition = positions[i];
  }

}
