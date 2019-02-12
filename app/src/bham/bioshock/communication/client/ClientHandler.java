package bham.bioshock.communication.client;

import bham.bioshock.communication.Action;

public interface ClientHandler {
  public void execute(Action action);
}
