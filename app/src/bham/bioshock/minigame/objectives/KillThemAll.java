package bham.bioshock.minigame.objectives;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Player;
import java.util.HashMap;


public class KillThemAll extends Objective {
  private Position respawnPosition = new Position(0, 0);
  private HashMap<Player, Float> health = new HashMap<>();
  private float initialHealth = 100.0f;

  public KillThemAll(){
    initialiseHealth();
  }

  @Override
  public Player getWinner() {
    return null;
  }
  

  public void initialiseHealth() {
    getPlayers().forEach(player -> health.put(player,initialHealth));
  }

  public float getPlayerHealth(Player p){
    return health.get(p);
  }

  public void setPlayerHealth(float newHealth, Player p){
    health.computeIfPresent(p, (k, v) -> newHealth);
  }

}
