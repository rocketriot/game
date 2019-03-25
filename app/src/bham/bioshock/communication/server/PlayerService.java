package bham.bioshock.communication.server;

import bham.bioshock.communication.common.Receiver;
import bham.bioshock.communication.common.Sender;
import bham.bioshock.communication.interfaces.ServerService;
import bham.bioshock.communication.interfaces.MessageHandler;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.server.InvalidMessageSequence;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Executes the actions received by ServerReceiver. */
public class PlayerService extends Thread implements MessageHandler, ServerService {
  private static final Logger logger = LogManager.getLogger(PlayerService.class);

  /** Thread sending messages */
  private Sender sender;
  /** Thread receiving messages */
  private Receiver receiver;

  /** Queue with received and not yet handled actions */
  private BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

  private MultipleConnectionsHandler handler;
  private Optional<UUID> id = Optional.empty();
  
  private ObjectInput fromClient;
  private ObjectOutput toClient;
  private boolean aborting = false;

  public PlayerService(ObjectInput fromClient, ObjectOutput toClient,
      MultipleConnectionsHandler handler) {
    super("ServerService");
    this.fromClient = fromClient;
    this.toClient = toClient;
    this.sender = new Sender(toClient);
    this.receiver = new Receiver(this, fromClient);

    this.handler = handler;
  }

  /** Save related player ID */
  public void saveId(UUID id, String username) {
    this.setName("ServerService - " + username);
    this.sender.setName("Server_Sender - " + username);
    this.sender.setName("Server_Receiver - " + username);
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
  
  /**
   * Get number of messages sender sent
   * @return
   */
  public long getSenderCounter() {
    return sender.getCounter();
  }
  
  /**
   * Reset sender counter
   */
  public void resetSenderCounter() {
    sender.resetCounter();
  }
  
  /**
   * Get sender queue size
   * @return
   */
  public int getSenderQueueSize() {
    return sender.getQueueSize();
  }

  @Override
  public void run() {
    // start supporting threads
    receiver.start();
    sender.start();

    try {
      while (!isInterrupted()) {
        try {
          // Execute actions from queue
          handler.handleRequest(queue.take(), this);
          
        } catch(InvalidMessageSequence e) {
          logger.catching(e);
        }
      }
    } catch (InterruptedException e) {
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
  public void handle(Message message) {
    queue.add(message);
  }
  
  /**
   * Stops threads and closes streams
   */
  @Override
  public void abort() {
    if(!aborting) {
      this.interrupt();
      this.aborting = true;
    }
  }
  
  /**
   * Checks if all related threads are down
   * 
   * @return true if everything is stopped
   */
  public boolean aborted() {
    return !isAlive() && !sender.isAlive() && !receiver.isAlive();
  }
  
  /**
   * Checks if all related threads are running
   * 
   * @return true if all threads are running
   */
  public boolean isRunning() {
    return isAlive() && sender.isAlive() && receiver.isAlive();
  }

  /**
   * Sends an action to the related client
   * 
   * @param action
   */
  public void send(Message message) {
    sender.send(message);
  }
  
  /**
   * Stop the service and underlying threads
   */
  private void close() {
    receiver.interrupt();
    sender.interrupt();
    
    try {
      sender.join();
    } catch (InterruptedException e) {
      logger.error("Unexpected interruption " + e.getMessage());
    }
  
    // Close incoming stream
    try {
      fromClient.close();        
    } catch (IOException e) {}
    // Close outgoing stream
    try {
      toClient.close();
    } catch (IOException e) {};
    
    try {
      receiver.join();
    } catch (InterruptedException e) {
      logger.error("Unexpected interruption " + e.getMessage());
    }
    
    logger.debug("Client disconnected");
  }
}
