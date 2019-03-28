package bham.bioshock.communication.messages.objectives;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class FlagOwnerUpdateMessage extends Message {

  private static final long serialVersionUID = 161193236939940483L;
  public final long created;
  public final UUID flagOwner;

  public FlagOwnerUpdateMessage(UUID flagOwner) {
    super(Command.MINIGAME_OBJECTIVE);
    this.flagOwner = flagOwner;
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }
}
