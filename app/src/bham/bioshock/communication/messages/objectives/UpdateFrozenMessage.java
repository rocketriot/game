package bham.bioshock.communication.messages.objectives;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class UpdateFrozenMessage extends Message {

    private final long created;
    public UUID playerID;
    public UpdateFrozenMessage(UUID playerID) {
    super(Command.MINIGAME_OBJECTIVE);

    this.playerID = playerID;
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);


    }
}
