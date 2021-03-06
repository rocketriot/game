package bham.bioshock.communication.common;

import bham.bioshock.communication.interfaces.MessageHandler;
import bham.bioshock.communication.messages.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInput;

public class Receiver extends Thread {
  private static final Logger logger = LogManager.getLogger(Receiver.class);

  private ObjectInput client;
  private MessageHandler actionHandler;

  /**
   * Constructs a new receiver
   *
   * @param actionHandler used when new action is received
   * @param client the reader with which this receiver will read data
   */
  public Receiver(MessageHandler actionHandler, ObjectInput client) {
    super("Receiver");
    this.client = client;
    this.actionHandler = actionHandler;
  }

  /** Reads messages from the stream and use registered action handler */
  public void run() {
    try {
      while (!isInterrupted()) {
        Message receivedMessage;
        try {
          receivedMessage = (Message) client.readObject();

          if (receivedMessage != null) {
            // execute business logic
            actionHandler.handle(receivedMessage);
          }

        } catch (ClassNotFoundException | ClassCastException e) {
          logger.error("Invalid message class!");
          continue;
        }
      }
    } catch (IOException e) {
      logger.error("Receiver (" + getId() + ") disconnected " + e.getMessage());
    } finally {
      actionHandler.abort();
    }

    logger.trace("Server receiver ending");
  }
}
