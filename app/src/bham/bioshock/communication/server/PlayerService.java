package bham.bioshock.communication.server;

import bham.bioshock.communication.common.Service;
import bham.bioshock.communication.interfaces.ServerService;
import bham.bioshock.communication.interfaces.MessageHandler;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.server.InvalidMessageSequence;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Executes the actions received by ServerReceiver. */
public class PlayerService extends Service implements MessageHandler, ServerService {
  private static final Logger logger = LogManager.getLogger(Service.class);
  
  private MultipleConnectionsHandler handler;
  private Optional<UUID> id = Optional.empty();
  
  private boolean aborting = false;

  public PlayerService(ObjectInput input, ObjectOutput output,
      MultipleConnectionsHandler handler) {
    super(input, output, "ServerService");
    
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
   * Sends an action to the related client
   * 
   * @param action
   */
  public void send(Message message) {
    sender.send(message);
  }
  
  @Override
  public void run() {
    super.run();
    
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
      logger.trace(getName() + " was interrupted");
    } finally {
      logger.trace(getName() + " ending");
      handler.unregister(this);
      close();
    }
    
  }
}
