package bham.bioshock.communication.messages.objectives;

import bham.bioshock.common.Position;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class KillAndRespawnMessage extends Message {

  private static final long serialVersionUID = -8062986162904767550L;

  public final long created;
  public final UUID playerId;
  public final UUID shooterId;
  public final Position position;

  public KillAndRespawnMessage(UUID playerId, UUID shooterId, Position position) {
    super(Command.MINIGAME_OBJECTIVE);
    this.playerId = playerId;
    this.shooterId = shooterId;
    this.position = position.copy();
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }
}
