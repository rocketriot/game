package bham.bioshock.communication.messages.boardgame;

import java.util.UUID;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class MovePlayerOnBoardMessage extends Message {

  private static final long serialVersionUID = -7154239854461234246L;
  
  public final UUID id;
  public final Coordinates coordinates;
  
  public MovePlayerOnBoardMessage(Coordinates coordinates, UUID playerId) {
    super(Command.MOVE_PLAYER_ON_BOARD);
    this.id = playerId;
    this.coordinates = coordinates.copy();
  }
}
