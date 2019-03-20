package bham.bioshock.communication.messages;

import java.util.UUID;
import bham.bioshock.client.screens.JoinScreen.RocketAnimation;
import bham.bioshock.common.Position;
import bham.bioshock.communication.Command;

public class JoinScreenMoveMessage extends Message {

  private static final long serialVersionUID = 176658910119037807L;

  public final UUID playerId;
  public final Position position;
  public final Float rotation;

  public JoinScreenMoveMessage(UUID playerId, RocketAnimation animation) {
    super(Command.JOIN_SCREEN_MOVE);
    this.playerId = playerId;
    this.position = animation.getPosition().copy();
    this.rotation = (float) animation.getRotation();
  }

}
