package bham.bioshock.communication.messages;

import java.util.UUID;
import bham.bioshock.common.Position;
import bham.bioshock.communication.Command;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.physics.SpeedVector;

public class BulletShotMessage extends Message {

  private static final long serialVersionUID = 8417992926192999146L;
  
  public final UUID bulletId;
  public final SpeedVector speedVector;
  public final Position position;
  
  public BulletShotMessage(Bullet bullet) {
    super(Command.MINIGAME_BULLET);
    this.bulletId = bullet.getId();
    this.speedVector = bullet.getSpeedVector();
    this.position = bullet.getPos();
  }

}
