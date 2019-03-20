package bham.bioshock.communication.messages;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import bham.bioshock.common.Position;
import bham.bioshock.communication.Command;
import bham.bioshock.minigame.ai.CpuAstronaut;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.physics.SpeedVector;

public class MinigamePlayerStepMessage extends Message {

  private static final long serialVersionUID = 7260121506206643960L;
  
  public final UUID playerId;
  public final SpeedVector speed;
  public final Position position;
  public final Boolean haveGun;
  public final long created;
  
  public MinigamePlayerStepMessage(Astronaut astronaut) {
    super(Command.MINIGAME_PLAYER_STEP);
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    this.playerId = astronaut.getId();
    this.speed = astronaut.getSpeedVector().copy();
    this.position = astronaut.getPos().copy();
    this.haveGun = astronaut.haveGun();
  }

  
  public MinigamePlayerStepMessage(CpuAstronaut astronaut) {
    super(Command.MINIGAME_PLAYER_STEP);
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    this.playerId = astronaut.get().getId();
    this.speed = astronaut.get().getSpeedVector().copy();
    this.position = astronaut.get().getPos().copy();
    this.haveGun = astronaut.get().haveGun();
  }
}
