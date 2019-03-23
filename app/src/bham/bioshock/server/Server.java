package bham.bioshock.server;

import java.io.IOException;
import java.net.ServerSocket;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Config;
import bham.bioshock.communication.server.CommunicationMaker;

@Singleton
public class Server {
  private ServerHandler handler;
  private CommunicationMaker connMaker;
  private ServerSocket serverSocket;
  private Store store;

  @Inject
  public Server(Store store) {
    this.store = store;
  }

  public Boolean start() {
    this.handler = new ServerHandler(store, this);
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
    if (connMaker != null) {
      connMaker.stopDiscovery();
    }
  }

  public void stop() {
    stopDiscovery();
    if(handler != null) {
      handler.stopAll();      
    }
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (IOException e) {
      }
    }
  }
}
