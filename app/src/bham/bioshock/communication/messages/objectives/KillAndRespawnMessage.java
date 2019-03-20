package bham.bioshock.communication.messages.objectives;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class KillAndRespawnMessage extends Message {

  private static final long serialVersionUID = -8062986162904767550L;

  public KillAndRespawnMessage() {
    super(Command.MINIGAME_OBJECTIVE);
  }

}
