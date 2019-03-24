package bham.bioshock.communication.messages.joinscreen;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class ReconnectPlayerMessage extends Message {

  private static final long serialVersionUID = -708780079889912741L;

  public ReconnectPlayerMessage() {
    super(Command.RECONNECT_PLAYER);

  }

}
