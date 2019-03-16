package bham.bioshock.communication.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.communication.Action;

public class Receiver extends Thread {
  private static final Logger logger = LogManager.getLogger(Receiver.class);
  
  private ObjectInputStream client;
  private ActionHandler actionHandler;

  /**
   * Constructs a new receiver
   * 
   * @param actionHandler used when new action is received
   * @param client the reader with which this receiver will read data
   */
  public Receiver(ActionHandler actionHandler, ObjectInputStream client) {
    this.client = client;
    this.actionHandler = actionHandler;
  }

  /**
   * Reads messages from the stream and use registered action handler
   */
  public void run() {
    try {
      while (!isInterrupted()) {
        Action userAction;
        try {
          userAction = (Action) client.readObject();
          // execute business logic
          actionHandler.handle(userAction);
          
        } catch (ClassNotFoundException | ClassCastException e) {
          logger.error("Invalid message class!");
          continue;
        }
      }
    } catch (IOException e) {
      logger.error("Receiver ("+getId()+") disconnected " + e.getMessage());
    } finally {
      actionHandler.abort();
    }

    logger.trace("Server receiver ending");
  }
}
