package bham.bioshock.communication.messages;

import java.util.UUID;
import bham.bioshock.communication.Command;

public class EndMinigameMessage extends Message {

  private static final long serialVersionUID = 5711324556429837592L;
  
  public final UUID playerId;
  public final UUID winnerId;
  public final UUID planetId;
  public final Boolean isCaptured;
  public final Integer points;
  
  
  public EndMinigameMessage(UUID playerId, UUID winnerId, UUID planetId, boolean isCaptured, int points) {
    super(Command.MINIGAME_END);
    this.playerId = playerId;
    this.winnerId = winnerId;
    this.planetId = planetId;
    this.isCaptured = isCaptured;
    this.points = points;
  }
}
