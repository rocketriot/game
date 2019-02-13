package bham.bioshock.server;

import com.google.inject.Singleton;
import bham.bioshock.common.models.Store;
import bham.bioshock.communication.server.CommunicationServer;
import bham.bioshock.communication.server.ServerHandler;

@Singleton
public class Server extends Thread {
  private Store store;
  private ServerHandler handler;

  public Server() {
    this.store = new Store();
    this.handler = new ServerHandler(store);
  }

  public void run() {
    CommunicationServer.start(handler);
  }
}
