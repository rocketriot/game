package bham.bioshock.communication.messages;

import java.util.UUID;
import bham.bioshock.communication.Command;

public class DisconnectPlayerMessage extends Message {

  private static final long serialVersionUID = -2122858266026288267L;
  
  public final UUID playerId;
  
  public DisconnectPlayerMessage(UUID playerId) {
    super(Command.REMOVE_PLAYER);
    this.playerId = playerId;
  }

}
