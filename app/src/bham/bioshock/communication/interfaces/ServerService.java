package bham.bioshock.communication.interfaces;

import bham.bioshock.communication.messages.Message;

import java.util.Optional;
import java.util.UUID;

public interface ServerService {

  /**
   * Save service id and name This makes the service identifiable
   *
   * @param id
   * @param username
   */
  void saveId(UUID id, String name);

  /**
   * Returns service Id
   *
   * @return
   */
  Optional<UUID> Id();

  /**
   * Send a message
   *
   * @param message to be sent
   */
  void send(Message message);

  /**
   * Get the size of the queue in underlying sender
   *
   * @return number of messages waiting
   */
  int getSenderQueueSize();

  /**
   * Get the number of messages sent by the underlying sender
   *
   * @return number of messages sent
   */
  long getSenderCounter();

  /** Resets message counter in underlying sender */
  void resetSenderCounter();

  /** Stop the service with underlying threads */
  void abort();

  /** Starts the service */
  void start();
}
