package bham.bioshock.communication.messages.objectives;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class UpdateHealthMessage extends Message {

  private static final long serialVersionUID = 6806469893148962075L;

  public final UUID playerId;
  public final UUID shooterId;
  public final long created;

  public UpdateHealthMessage(UUID playerId, UUID shooterId) {
    super(Command.MINIGAME_OBJECTIVE);
    this.playerId = playerId;
    this.shooterId = shooterId;
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }
}
