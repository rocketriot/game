package bham.bioshock.communication.client;

import bham.bioshock.communication.messages.Message;

public interface IClientHandler {
  public void execute(Message message);
}
