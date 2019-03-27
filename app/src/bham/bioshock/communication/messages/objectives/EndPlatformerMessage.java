package bham.bioshock.communication.messages.objectives;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

import java.util.UUID;

public class EndPlatformerMessage extends Message {
    public final UUID winnerID;

    public EndPlatformerMessage(UUID winnerID) {
        super(Command.MINIGAME_OBJECTIVE);
        this.winnerID =  winnerID;

    }
}
