package bham.bioshock.communication.client;

import bham.bioshock.communication.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Interprets commands received from server */
public class ClientService extends Thread implements IClientService {

  private static final Logger logger = LogManager.getLogger(ClientService.class);
  
  private ClientSender sender;
  private ClientReceiver receiver;
  private Socket server;
  private ObjectInputStream fromServer;
  private ObjectOutputStream toServer;

  private PriorityBlockingQueue<Action> queue = new PriorityBlockingQueue<>();
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
    receiver = new ClientReceiver(this, fromServer);
    sender = new ClientSender(toServer);
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
      while (true) {
        // Execute action from a blocking queue
        if(handler != null) {
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

  public void store(Action action) {
    queue.add(action);
  }

  /**
   * Send the action to the server
   *
   * @param action to be sent
   */
  public void send(Action action) {
    if(!isCreated()) {
      logger.fatal("ClientService was not created! Message won't be sent!");
      return;
    }
    sender.send(action);
  }

  /** Wait for the threads to terminate and than close the sockets and streams */
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
    logger.debug("Client ended. Goodbye.");
  }
}
