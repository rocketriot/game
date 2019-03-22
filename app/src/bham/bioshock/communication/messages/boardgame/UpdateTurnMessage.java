package bham.bioshock.communication.messages.boardgame;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class UpdateTurnMessage extends Message {

  private static final long serialVersionUID = 1748486523141727584L;

  public UpdateTurnMessage() {
    super(Command.UPDATE_TURN);
  }

}
