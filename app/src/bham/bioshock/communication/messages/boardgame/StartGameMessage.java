package bham.bioshock.communication.messages;

import bham.bioshock.communication.Command;

public class StartGameMessage extends Message {
  
  private static final long serialVersionUID = -5809185135635387262L;

  public StartGameMessage() {
    super(Command.START_GAME);
  }

}
