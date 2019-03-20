package bham.bioshock.communication.messages.objectives;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class SubstractHealthMessage extends Message {

  private static final long serialVersionUID = 6806469893148962075L;
  
  public final UUID playerId;
  public final UUID shooterId;
  public final long created;
  
  public SubstractHealthMessage(UUID playerId, UUID shooterId) {
    super(Command.MINIGAME_OBJECTIVE);
    this.playerId = playerId;
    this.shooterId = shooterId;
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }

}
