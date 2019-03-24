package bham.bioshock.communication.messages.objectives;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class IncreaseHealthMessage extends Message {

  private static final long serialVersionUID = 6806469893148362075L;

  public final UUID playerId;
  public final long created;

  public IncreaseHealthMessage(UUID playerId) {
    super(Command.MINIGAME_OBJECTIVE);
    this.playerId = playerId;
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }

}
