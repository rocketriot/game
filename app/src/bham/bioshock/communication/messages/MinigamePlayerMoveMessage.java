package bham.bioshock.communication.messages;

import java.util.UUID;
import bham.bioshock.common.Position;
import bham.bioshock.communication.Command;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Astronaut.Move;
import bham.bioshock.minigame.physics.SpeedVector;

public class MinigamePlayerMoveMessage extends Message {

  private static final long serialVersionUID = 1188177011486338949L;
  public final UUID playerId;
  public final SpeedVector speed;
  public final Position position;
  public final Move move;
  public final Boolean haveGun;
  
  public MinigamePlayerMoveMessage(Astronaut astronaut) {
    super(Command.MINIGAME_PLAYER_MOVE);
    
    this.playerId = astronaut.getId();
    this.speed = astronaut.getSpeedVector();
    this.position = astronaut.getPos();
    this.move = astronaut.getMove();
    this.haveGun = astronaut.haveGun();
  }

}
