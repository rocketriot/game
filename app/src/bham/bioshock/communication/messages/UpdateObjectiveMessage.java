package bham.bioshock.communication.messages;

import bham.bioshock.communication.Command;
import bham.bioshock.minigame.objectives.Objective;

public class UpdateObjectiveMessage extends Message {

  private static final long serialVersionUID = -1679137637224356213L;

  public final Objective objective;
  
  public UpdateObjectiveMessage(Objective objective) {
    super(Command.MINIGAME_UPDATE_OBJECTIVE);
    this.objective = objective.clone();
  }
}
