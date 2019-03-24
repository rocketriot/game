package bham.bioshock.communication.client;

import bham.bioshock.communication.messages.Message;

public interface IClientService {

  public void send(Message message);
}
