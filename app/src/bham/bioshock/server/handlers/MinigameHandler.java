package bham.bioshock.server.handlers;

import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.server.ServerHandler;

public class MinigameHandler {

  Store store;
  ServerHandler handler;
  
  public MinigameHandler (Store store, ServerHandler handler) {
    this.store = store;
    this.handler = handler;
  }
  
  
  public static void playerMove() {
    
  }
}
