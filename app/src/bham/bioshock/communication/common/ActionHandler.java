package bham.bioshock.communication.common;

import bham.bioshock.communication.Action;

public interface ActionHandler {
  
  /**
   * Executes business logic for the provided action
   * 
   * @param action
   */
  public void handle(Action action);
  
  /**
   * Stops handling actions
   */
  public void abort();
}
