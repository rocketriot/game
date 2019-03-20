package bham.bioshock.communication.server;

import bham.bioshock.communication.Action;
import bham.bioshock.communication.common.ActionHandler;
import bham.bioshock.communication.common.Receiver;
import bham.bioshock.communication.common.Sender;
import bham.bioshock.server.ServerHandler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Executes the actions received by ServerReceiver. */
public class ServerService extends Thread implements ActionHandler {
  private static final Logger logger = LogManager.getLogger(ServerService.class);

  /** Thread sending messages */
  private Sender sender;
  /** Thread receiving messages */
  private Receiver receiver;

  /** Queue with received and not yet handled actions */
  private BlockingQueue<Action> queue = new LinkedBlockingQueue<>();

  private ServerHandler handler;
  private Optional<UUID> id = Optional.empty();
  
  private ObjectInputStream fromClient;
  private ObjectOutputStream toClient;

  public ServerService(ObjectInputStream fromClient, ObjectOutputStream toClient,
      ServerHandler handler) {

    this.fromClient = fromClient;
    this.toClient = toClient;
    this.sender = new Sender(toClient);
    this.receiver = new Receiver(this, fromClient);

    this.handler = handler;
  }

  /** Save related player ID */
  public void saveId(UUID id) {
    this.id = Optional.of(id);
  }

  /**
   * Returns related player ID
   * 
   * @return ID of the related player
   */
  public Optional<UUID> Id() {
    return id;
  }
  
  public long getSenderCounter() {
    return sender.getCounter();
  }
  
  public void resetSenderCounter() {
    sender.resetCounter();
  }
  
  public int getSenderQueueSize() {
    return sender.getQueueSize();
  }

  public void run() {
    // start supporting threads
    receiver.start();
    sender.start();

    try {
      while (!isInterrupted()) {
        // Execute actions from queue
        execute(queue.take());
      }
    } catch (InterruptedException e) {
      receiver.interrupt();
      sender.interrupt();
      logger.trace("ServerService was interrupted");
    } finally {
      logger.trace("ServerService ending");
      handler.unregister(this);
      close();
    }
  }

  /**
   * Adds new action to the waiting queue
   * 
   * @param action
   */
  @Override
  public void handle(Action action) {
    queue.add(action);
  }
  
  /**
   * Stops threads and closes streams
   */
  @Override
  public void abort() {
    this.interrupt();
  }

  /**
   * Sends an action to the related client
   * 
   * @param action
   */
  public void send(Action action) {
    sender.send(action);
  }


  /**
   * Delegates the execution to the appropriate method
   * 
   * @param action to be executed
   */
  private void execute(Action action) {
    handler.handleRequest(action, this);
  }
  
  private void close() {
    try {
      sender.join();
      // Close incoming stream
      try {
        fromClient.close();        
      } catch (IOException e) {}
      // Close outgoing stream
      try {
        toClient.close();
      } catch (IOException e) {};
      receiver.join();
    } catch (InterruptedException e) {
      logger.error("Unexpected interruption " + e.getMessage());
    }
    logger.debug("Client disconnected");
  }
}
