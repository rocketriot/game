package bham.bioshock.communication.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.communication.interfaces.MessageHandler;
import bham.bioshock.communication.messages.Message;

public abstract class Service extends Thread implements MessageHandler {

  private static final Logger logger = LogManager.getLogger(Service.class);
  
  /** Queue with received and not yet handled actions */
  protected BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
  
  /** Thread sending messages */
  protected Sender sender;
  /** Thread receiving messages */
  protected Receiver receiver;
  
  private ObjectInput input;
  private ObjectOutput output;
  
  public Service(ObjectInput input, ObjectOutput output, String name) {
    super(name);
    this.input = input;
    this.output = output;
    this.sender = new Sender(output);
    this.receiver = new Receiver(this, input);
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
   * Create supporting threads
   */
  @Override
  public void run() {
    // start supporting threads
    receiver.start();
    sender.start();
  }
  
  /**
   * Stop the service and underlying threads
   */
  protected void close() {
    receiver.interrupt();
    sender.interrupt();
    
    try {
      sender.join();
    } catch (InterruptedException e) {
      logger.error("Unexpected interruption " + e.getMessage());
    }
  
    // Close incoming stream
    try {
      input.close();        
    } catch (IOException e) {}
    // Close outgoing stream
    try {
      output.close();
    } catch (IOException e) {};
    
    try {
      receiver.join();
    } catch (InterruptedException e) {
      logger.error("Unexpected interruption " + e.getMessage());
    }
    
    logger.debug("Client disconnected");
  }

  /**
   * Finish thread loop, close all related threads and connections
   */
  @Override
  public void abort() {
    this.interrupt();
  }
}
