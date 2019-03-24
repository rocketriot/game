package bham.bioshock.communication.interfaces;

import java.util.Optional;
import java.util.UUID;
import bham.bioshock.communication.messages.Message;

public interface ServerService {
  
  /**
   * Save service id and name
   * This makes the service identifiable
   * 
   * @param id
   * @param username
   */
  public void saveId(UUID id, String name);

  /**
   * Returns service Id
   * 
   * @return
   */
  public Optional<UUID> Id();

  /**
   * Send a message
   * 
   * @param message to be sent
   */
  public void send(Message message);

  
  /**
   * Get the size of the queue in underlying sender
   * 
   * @return number of messages waiting
   */
  public int getSenderQueueSize();

  /**
   * Get the number of messages sent by the underlying sender
   * 
   * @return number of messages sent
   */
  public long getSenderCounter();

  /**
   * Resets message counter in underlying sender
   */
  public void resetSenderCounter();

  /**
   * Stop the service with underlying threads
   */
  public void abort();

}
