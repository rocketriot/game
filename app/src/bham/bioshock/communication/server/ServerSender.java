package bham.bioshock.communication.server;

import bham.bioshock.communication.Action;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ServerSender {
  private ObjectOutputStream client;

  /**
   * Constructs a new server sender.
   *
   * @param toClient client output stream
   */
  public ServerSender(ObjectOutputStream toClient) {
    this.client = toClient;
  }

  /**
   * Send an action the the client
   *
   * @param action
   */
  public void send(Action action) {
    try {
      client.writeObject(action);
    } catch (IOException e) {
      System.err.println("Can't send a message " + e.getMessage());
    }
  }
}
