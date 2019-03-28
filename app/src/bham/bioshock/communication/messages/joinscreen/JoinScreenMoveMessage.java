package bham.bioshock.communication.messages.joinscreen;

import bham.bioshock.common.Position;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class JoinScreenMoveMessage extends Message {

  private static final long serialVersionUID = 176658910119037807L;

  public final UUID playerId;
  public final Position position;
  public final Float rotation;
  public final Long created;

  public JoinScreenMoveMessage(UUID playerId, Position position, double rotation) {
    super(Command.JOIN_SCREEN_MOVE);
    this.playerId = playerId;
    this.position = position.copy();
    this.rotation = (float) rotation;
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }
}
