package bham.bioshock.communication.messages.objectives;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

import java.util.UUID;

public class EndPlatformerMessage extends Message {

  private static final long serialVersionUID = -5512477239036736005L;
  public final UUID winnerID;

  public EndPlatformerMessage(UUID winnerID) {
    super(Command.MINIGAME_OBJECTIVE);
    this.winnerID = winnerID;
  }
}
