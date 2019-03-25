package bham.bioshock.communication.messages.joinscreen;

import java.util.UUID;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class ReconnectMessage extends Message {

  private static final long serialVersionUID = -708780079889912741L;

  public final UUID playerId;
  
  public ReconnectMessage(UUID playerId) {
    super(Command.RECONNECT_PLAYER);
    this.playerId = playerId;
  }

}
