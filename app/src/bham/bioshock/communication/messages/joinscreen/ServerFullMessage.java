package bham.bioshock.communication.messages.joinscreen;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class ServerFullMessage extends Message {
  private static final long serialVersionUID = -7278781382914867490L;

  public ServerFullMessage() {
    super(Command.SERVER_FULL);
  }
  
}
