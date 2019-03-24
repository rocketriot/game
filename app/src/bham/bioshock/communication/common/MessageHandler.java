package bham.bioshock.communication.common;

import bham.bioshock.communication.messages.Message;

public interface MessageHandler {
  
  /**
   * Executes business logic for the provided action
   * 
   * @param action
   */
  public void handle(Message action);
  
  /**
   * Stops handling actions
   */
  public void abort();
}
