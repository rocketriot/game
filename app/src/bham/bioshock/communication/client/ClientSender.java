package bham.bioshock.communication.client;

import bham.bioshock.communication.Action;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Reads from user console, checks if the provided command is valid i.e. if is known and if user is
 * allowed to use it (is logged in / logged out)
 */
public class ClientSender extends Thread {

  private ObjectOutputStream toServer;
  private BlockingQueue<Action> queue = new LinkedBlockingQueue<>();

  ClientSender(ObjectOutputStream _toServer) {
    toServer = _toServer;
  }

  public void send(Action action) {
    queue.add(action);
  }

  /** Start ClientSender thread. */
  public void run() {
    try {
      while (true) {
        Action action = queue.take();
        // send an action to the server
        toServer.writeObject(action);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Communication broke in ClientSender" + e.getMessage());
    }

    System.out.println("Client sender thread ending");
  }

}
