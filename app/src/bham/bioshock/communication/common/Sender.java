package bham.bioshock.communication.common;

import bham.bioshock.communication.messages.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender extends Thread {
  private static final Logger logger = LogManager.getLogger(Sender.class);

  private ObjectOutput client;
  private long counter = 0;
  private BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

  /**
   * Constructs a new sender with valid output stream
   *
   * @param toClient client output stream
   */
  public Sender(ObjectOutput toClient) {
    super("Sender");
    this.client = toClient;
  }

  /**
   * Add action to the queue for outgoing messages
   *
   * @param message
   */
  public void send(Message message) {
    queue.add(message);
  }

  public long getCounter() {
    return counter;
  }

  public void resetCounter() {
    counter = 0;
  }

  /**
   * Returns the size of the queue of messages waiting to be sent
   *
   * @return size
   */
  public int getQueueSize() {
    return queue.size();
  }

  /** Reads messages from the queue and sends them to the client */
  public void run() {
    try {
      while (!isInterrupted()) {
        Message message = queue.take();
        counter++;
        try {
          client.writeObject(message);
        } catch (IOException e) {
          logger.error("Message can't be sent! " + e.getMessage());
        }
      }
    } catch (InterruptedException e) {
      logger.trace("Sender ending");
    }
  }
}
