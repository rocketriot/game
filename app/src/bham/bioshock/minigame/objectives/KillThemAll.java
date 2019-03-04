package bham.bioshock.minigame.objectives;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Player;
import java.util.Collection;


public class KillThemAll extends Objective {
  private Position respawnPosition = new Position(-2300, 0);

  public KillThemAll(Collection<Player> players, Player mainPlayer){
    super(players, mainPlayer);
  }

  @Override
  public Player getWinner() {
    return null;
  }

  @Override
  public void handleDead() {
    for(Player player : getPlayers())
      if(player.isDead){
        player.setHealth(getInitialHealth());
        player.setPosition(respawnPosition);
      }
  }


}
