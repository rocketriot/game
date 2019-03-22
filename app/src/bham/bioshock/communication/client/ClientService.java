package bham.bioshock.communication.client;

import bham.bioshock.communication.common.MessageHandler;
import bham.bioshock.communication.common.Receiver;
import bham.bioshock.communication.common.Sender;
import bham.bioshock.communication.messages.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Interprets commands received from server */
public class ClientService extends Thread implements IClientService, MessageHandler {

  private static final Logger logger = LogManager.getLogger(ClientService.class);

  private Sender sender;
  private Receiver receiver;
  private Socket socket;
  private ObjectInputStream fromServer;
  private ObjectOutputStream toServer;

  private BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
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
  public ClientService(Socket socket, ObjectInputStream fromServer, ObjectOutputStream toServer) {
    connectionCreated = true;
    // save socket and streams for communication
    this.socket = socket;
    this.fromServer = fromServer;
    this.toServer = toServer;

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
      logger.trace("Client service was interrupted");
    } finally {
      // wait for the threads to terminate and close the streams
      close();
    }

  }

  public void registerHandler(IClientHandler handler) {
    this.handler = handler;
  }

  @Override
  public void handle(Message action) {
    queue.add(action);
  }

  /**
   * Send the action to the server
   *
   * @param action to be sent
   */
  public void send(Message message) {
    if (!isCreated()) {
      logger.fatal("ClientService was not created! Message won't be sent!");
      return;
    }
    sender.send(message);
  }

  /**
   * Server disconnected, close connection
   */
  @Override
  public void abort() {
    this.interrupt();
  }

  /**
   * Wait for the threads to terminate and than close the sockets and streams
   */
  private void close() {
    try {
      sender.interrupt();
      sender.join();
      logger.debug("Client sender ended");
      // Try to close all used sockets. Ignore errors
      try {
        toServer.close();
      } catch (IOException e) {
      };
      try {
        fromServer.close();
      } catch (IOException e) {
      };
      try {
        socket.close();
      } catch (IOException e) {
      };

      receiver.join();
      logger.debug("Client receiver ended");
    } catch (InterruptedException e) {
      logger.error("Unexpected interruption " + e.getMessage());
    }
    logger.debug("Client disconnected");
    connectionCreated = false;
  }

}
