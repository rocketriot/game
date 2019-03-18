package bham.bioshock.communication.messages;

import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Command;

public class MovePlayerOnBoardMessage extends Message {

  private static final long serialVersionUID = -7154239854461234246L;
  private Player player;
  
  public MovePlayerOnBoardMessage(Player movingPlayer) {
    super(Command.MOVE_PLAYER_ON_BOARD);
    this.player = movingPlayer;
  }
  
  public Player getPlayer() {
    return player;
  }
}
