package bham.bioshock.communication.messages.minigame;

import java.util.UUID;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class RequestMinigameStartMessage extends Message {

  private static final long serialVersionUID = 2557419532991502272L;

  public final UUID planetId;
  
  public RequestMinigameStartMessage(UUID planetId) {
    super(planetId == null ? Command.MINIGAME_DIRECT_START : Command.MINIGAME_START);
    this.planetId = planetId;
  }

}