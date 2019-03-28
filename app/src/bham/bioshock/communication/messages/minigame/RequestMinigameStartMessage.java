package bham.bioshock.communication.messages.minigame;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

import java.util.UUID;

public class RequestMinigameStartMessage extends Message {

  private static final long serialVersionUID = 2557419532991502272L;

  public final UUID planetId;
  public final Integer objectiveId;

  public RequestMinigameStartMessage(UUID planetId, Integer objectiveId) {
    super(Command.MINIGAME_START);
    this.planetId = planetId;
    this.objectiveId = objectiveId;
  }

  public RequestMinigameStartMessage(UUID planetId) {
    super(Command.MINIGAME_START);
    this.planetId = planetId;
    this.objectiveId = null;
  }

  public RequestMinigameStartMessage() {
    super(Command.MINIGAME_DIRECT_START);
    this.planetId = null;
    this.objectiveId = null;
  }
}
