package bham.bioshock.communication.client;

import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.common.ActionHandler;
import bham.bioshock.communication.common.Receiver;
import bham.bioshock.communication.common.Sender;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Interprets commands received from server */
public class ClientService extends Thread implements IClientService, ActionHandler {

  private static final Logger logger = LogManager.getLogger(ClientService.class);

  private Sender sender;
  private Receiver receiver;
  private Socket server;
  private ObjectInputStream fromServer;
  private ObjectOutputStream toServer;

  private BlockingQueue<Action> queue = new LinkedBlockingQueue<>();
  private IClientHandler handler;
  private boolean connectionCreated = false;

  /**
   * Creates the service and helper objects to send and receive messages
   *
   * @param _server socket
   * @param fromServer stream from server
   * @param toServer stream to server
   * @param client main client
   */
  public void create(Socket _server, ObjectInputStream _fromServer, ObjectOutputStream _toServer) {
    connectionCreated = true;
    // save socket and streams for communication
    server = _server;
    fromServer = _fromServer;
    toServer = _toServer;

    // Create two client object to send and receive messages
    receiver = new Receiver(this, fromServer);
    sender = new Sender(toServer);
  }

  public boolean isCreated() {
    return connectionCreated;
  }

  /** Starts the sender and receiver threads */
  public void run() {
    // Run sender and receiver in parallel:
    sender.start();
    receiver.start();

    try {
      while (!isInterrupted()) {
        // Execute action from a blocking queue
        if (handler != null) {
          handler.execute(queue.take());
        }
      }
    } catch (InterruptedException e) {
      logger.error("Client service was interrupted");
    }

    // wait for the threads to terminate and close the streams
    close();
  }

  public void registerHandler(IClientHandler handler) {
    this.handler = handler;
  }

  @Override
  public void handle(Action action) {
    queue.add(action);
  }

  /**
   * Send the action to the server
   *
   * @param action to be sent
   */
  public void send(Action action) {
    if (!isCreated()) {
      logger.fatal("ClientService was not created! Message won't be sent!");
      return;
    }
    sender.send(action);
    
    if(action.getCommand().equals(Command.DISCONNECT)) {
      sender.interrupt();
      close();
    }
  }

  /**
   *  Wait for the threads to terminate and than close the sockets and streams 
   */
  public void close() {
    try {
      sender.join();
      logger.debug("Client sender ended");
      toServer.close();
      fromServer.close();
      server.close();
      receiver.join();
      logger.debug("Client receiver ended");
    } catch (IOException e) {
      logger.error("Something wrong " + e.getMessage());
      System.exit(0);
    } catch (InterruptedException e) {
      logger.error("Unexpected interruption " + e.getMessage());
      System.exit(0);
    }
    logger.debug("Client disconnected");
  }

}
