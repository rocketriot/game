package bham.bioshock.communication.common;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.communication.Action;

public class Sender extends Thread {
  private static final Logger logger = LogManager.getLogger(Sender.class);
  
  private ObjectOutputStream client;
  private long counter = 0;
  private BlockingQueue<Action> queue = new LinkedBlockingQueue<>();

  /**
   * Constructs a new sender with valid output stream
   *
   * @param toClient client output stream
   */
  public Sender(ObjectOutputStream toClient) {
    this.client = toClient;
  }

  /**
   * Add action to the queue for outgoing messages
   *
   * @param action
   */
  public void send(Action action) {
    queue.add(action);
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

  /**
   * Reads messages from the queue and sends them to the client
   */
  public void run() {
    try {
      while (!isInterrupted()) {
        Action action = queue.take();
        counter++;
        try {
          client.writeObject(action);
        } catch (IOException e) {
          logger.error("Message can't be sent! " + e.getMessage());
        }
      }
    } catch (InterruptedException e) {
      logger.trace("Sender ending");
    }
  }
}
