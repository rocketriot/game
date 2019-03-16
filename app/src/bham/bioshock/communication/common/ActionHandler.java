package bham.bioshock.communication.common;

import bham.bioshock.communication.Action;

public interface ActionHandler {
  
  /**
   * Execute business logic for the provided action
   * 
   * @param action
   */
  public void handle(Action action);
}
