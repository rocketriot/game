package bham.bioshock.minigame.objectives;
import bham.bioshock.minigame.models.Player;
import java.util.ArrayList;


public class KillThemAll extends Objective {

  public KillThemAll(ArrayList<Player> players, Player mainPlayer){
    super(players, mainPlayer);
  }



  @Override
  public Player getWinner() {
    return null;
  }


}
