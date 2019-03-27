package bham.bioshock.communication.interfaces;

import bham.bioshock.communication.messages.Message;

public interface MessageHandler {
  
  /**
   * Executes business logic for the provided action
   * 
   * @param action
   */
  void handle(Message action);
  
  /**
   * Stops handling actions
   */
  void abort();
}
