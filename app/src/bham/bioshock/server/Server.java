package bham.bioshock.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.server.CommunicationServer;
import bham.bioshock.communication.server.ServerHandler;

@Singleton
public class Server extends Thread {
  private ServerHandler handler;
  
  @Inject
  public Server(Store store) {
    this.handler = new ServerHandler(store);
  }

  public void run() {
    CommunicationServer.start(handler);
  }
}
