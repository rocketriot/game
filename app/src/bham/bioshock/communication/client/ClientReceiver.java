package bham.bioshock.communication.client;

import bham.bioshock.communication.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;

/**
 * Gets actions from the server and transfers it to ClientService Can be used by service to print
 * messages to the user
 */
public class ClientReceiver extends Thread {

  private ObjectInputStream fromServer;
  private ClientService service;

  ClientReceiver(ClientService service, ObjectInputStream fromServer) {
    this.fromServer = fromServer;
    this.service = service;
  }

  /** Run the client receiver thread. */
  public void run() {
    try {
      // read messages from the server
      while (true) {
        Action action;

        // Try to convert the object to Action
        try {
          action = (Action) fromServer.readObject();
        } catch (ClassNotFoundException e) {
          // Write an error if object is incorrect
          System.err.println("Invalid Action class in ClientReceiver");
          continue;
        }

        // This will execute the business logic in the service.
        service.store(action);
      }
    } catch (SocketException e) {
      System.out.println("Client receiver ending");
    } catch (NullPointerException | IOException e) {
      System.err
          .println("Server seems to have died " + (e.getMessage() == null ? "" : e.getMessage()));
      System.exit(0);
    }
  }
}
