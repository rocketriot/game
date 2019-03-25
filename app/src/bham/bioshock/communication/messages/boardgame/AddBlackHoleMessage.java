package bham.bioshock.communication.messages.boardgame;

import bham.bioshock.common.models.Coordinates;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class AddBlackHoleMessage extends Message {

  private static final long serialVersionUID = -7154239854461234246L;
  
  public final Coordinates coordinates;
  
  public AddBlackHoleMessage(Coordinates coordinates) {
    super(Command.ADD_BLACK_HOLE);
    this.coordinates = coordinates.copy();
  }
}
