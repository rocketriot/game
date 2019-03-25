package bham.bioshock.communication.messages.objectives;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class IncreaseHealthMessage extends Message {

  private static final long serialVersionUID = 6806469893148362075L;

  public final UUID playerID;
  public final UUID heartID;
  public final long created;

  public IncreaseHealthMessage(UUID playerID, UUID heartID) {
    super(Command.MINIGAME_OBJECTIVE);
    this.playerID = playerID;
    this.heartID = heartID;
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }

}
