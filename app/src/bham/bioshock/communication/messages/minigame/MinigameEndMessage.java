package bham.bioshock.communication.messages.minigame;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class MinigameEndMessage extends Message {

  private static final long serialVersionUID = -4593583024962807900L;

  public MinigameEndMessage() {
    super(Command.MINIGAME_END);
  }
}
