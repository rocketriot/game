package bham.bioshock.communication.messages.minigame;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.minigame.models.Entity;

public class SpawnEntityMessage extends Message {

  private static final long serialVersionUID = 8458979692585491952L;

  public final Entity entity;

  public SpawnEntityMessage(Entity e) {
    super(Command.MINIGAME_SPAWN);
    this.entity = e;
  }
}
