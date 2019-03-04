package bham.bioshock.server;

import java.io.IOException;
import java.net.ServerSocket;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Config;
import bham.bioshock.communication.server.CommunicationMaker;
import bham.bioshock.communication.server.ServerHandler;

@Singleton
public class Server {
  private ServerHandler handler;
  private CommunicationMaker connMaker;
  private ServerSocket serverSocket;
  
  @Inject
  public Server(Store store) {
    this.handler = new ServerHandler(store, this);
  }
  
  public Boolean start() {
    try {
      serverSocket = new ServerSocket(Config.PORT);
    } catch (IOException e1) {
      return false;
    }
    connMaker = new CommunicationMaker();
    connMaker.startSearch(handler, serverSocket);

    return true;
  }
  
  public void stopDiscovery() {
    connMaker.stopDiscovery();
  }
  
  public void stop() {
    try {
      serverSocket.close();
    } catch (IOException e) {
    }
  }
}
