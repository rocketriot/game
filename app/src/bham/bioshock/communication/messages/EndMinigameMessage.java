package bham.bioshock.communication.messages;

import bham.bioshock.communication.Command;
import java.util.UUID;

public class EndMinigameMessage extends Message {

  public final UUID playerID;
  public final UUID winnerID;
  public final UUID planetID;
  public final boolean initiatorWon;
  public final int points;

  public EndMinigameMessage(UUID playerID, UUID winnerID, UUID planetID, boolean initiatorWon, int points) {
    super(Command.MINIGAME_END);
    this.playerID = playerID;
    this.winnerID = winnerID;
    this.planetID = planetID;
    this.initiatorWon = initiatorWon;
    this.points = points;
  }
}
