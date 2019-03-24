package bham.bioshock.communication.messages;

import java.io.Serializable;
import bham.bioshock.communication.Command;

public abstract class Message implements Serializable {

  private static final long serialVersionUID = 8997264336447989514L;
  public final Command command;

  public Message(Command command) {
    this.command = command;
  }
}
