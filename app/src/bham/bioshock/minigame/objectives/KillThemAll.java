package bham.bioshock.minigame.objectives;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Astronaut;
import java.util.Collection;


public class KillThemAll extends Objective {
  private Position respawnPosition = new Position(-2300, 0);

  public KillThemAll(Collection<Astronaut> players, Astronaut mainPlayer){
    super(players, mainPlayer);
  }

  @Override
  public Astronaut getWinner() {
    return null;
  }

  @Override
  public void handleDead() {
    for(Astronaut player : getPlayers())
      if(player.isDead){
        player.setHealth(getInitialHealth());
        player.setPosition(respawnPosition);
      }
  }


}
