package bham.bioshock.communication.messages;

import bham.bioshock.communication.Command;

public class EndTurnMessage extends Message {

  private static final long serialVersionUID = -1750901284597220200L;
  
  public EndTurnMessage() {
    super(Command.END_TURN);
  }

}
