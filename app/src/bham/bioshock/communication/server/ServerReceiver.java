package bham.bioshock.communication.server;

import bham.bioshock.communication.Action;

import java.io.IOException;
import java.io.ObjectInputStream;

/** Gets actions from client and transfer it to ServerService */
public class ServerReceiver extends Thread {
  private ObjectInputStream client;
  private ServerService service;

  /**
   * Constructs a new server receiver.
   *
   * @param client the reader with which this receiver will read data
   */
  public ServerReceiver(ServerService service, ObjectInputStream client) {
    this.client = client;
    this.service = service;
  }

  /** Starts this server receiver. */
  public void run() {
    try {
      while (true) {
        Action userAction;

        try {
          userAction = (Action) client.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
          System.err.println("Invalid Action class in ServerReceiver");
          continue;
        }
        // execute business logic
        service.store(userAction);
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Something went wrong with the client " + e.getMessage());
      // No point in trying to close sockets. Just give up.
    }

    System.out.println("Server receiver ending");
  }
}
