package bham.bioshock.communication.messages;

import bham.bioshock.communication.Command;

import java.io.Serializable;

public abstract class Message implements Serializable {

  private static final long serialVersionUID = 8997264336447989514L;
  public final Command command;

  public Message(Command command) {
    this.command = command;
  }
}
