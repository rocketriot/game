package bham.bioshock.communication.messages.minigame;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;
import java.util.UUID;

public class EndMinigameMessage extends Message {
  private static final long serialVersionUID = 5711324556429837592L;

  public final UUID playerID;
  public final UUID winnerID;
  public final UUID planetID;
  public final boolean initiatorWon;
  public final int points;

  public EndMinigameMessage(UUID playerID, UUID winnerID, UUID planetID, int points) {
    super(Command.MINIGAME_END);
    this.playerID = playerID;
    this.winnerID = winnerID;
    this.planetID = planetID;
    this.points = points;
    
    if (winnerID != null && winnerID.equals(playerID)) {
      initiatorWon = true;
    } else {
      initiatorWon = false;
    }
  }
}
