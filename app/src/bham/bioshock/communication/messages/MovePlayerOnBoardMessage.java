package bham.bioshock.communication.messages;

import java.util.UUID;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.communication.Command;

public class MovePlayerOnBoardMessage extends Message {

  private static final long serialVersionUID = -7154239854461234246L;
  
  private UUID id;
  private Coordinates c;
  
  public MovePlayerOnBoardMessage(Coordinates coordinates, UUID playerId) {
    super(Command.MOVE_PLAYER_ON_BOARD);
    this.id = playerId;
    this.c = coordinates;
  }
  
  public UUID getId() {
    return id;
  }
  
  public Coordinates getCoordinates() {
    return c;
  }
}
