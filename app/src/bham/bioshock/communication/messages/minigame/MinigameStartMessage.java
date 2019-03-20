package bham.bioshock.communication.messages.minigame;

import java.util.UUID;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.worlds.World;

public class MinigameStartMessage extends Message {

  private static final long serialVersionUID = 2557419532991502272L;
  
  public final World world;
  public final Objective objective;
  
  public MinigameStartMessage(World w, Objective o) {
    super(Command.MINIGAME_START);
    this.world = w;
    this.objective = o;
  }

}
