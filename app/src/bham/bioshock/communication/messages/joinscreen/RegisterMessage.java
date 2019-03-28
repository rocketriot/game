package bham.bioshock.communication.messages.joinscreen;

import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class RegisterMessage extends Message {

  private static final long serialVersionUID = 8243080569230835935L;

  public final Player player;

  public RegisterMessage(Player player) {
    super(Command.REGISTER);
    this.player = player;
  }
}
