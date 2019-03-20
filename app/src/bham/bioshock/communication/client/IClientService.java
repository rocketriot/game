package bham.bioshock.communication.client;

import bham.bioshock.communication.Action;
import bham.bioshock.communication.messages.Message;

public interface IClientService {

  public void send(Action action);
  
  public void send(Message message);
}
