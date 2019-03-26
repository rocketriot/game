package bham.bioshock.server;

import java.io.IOException;
import java.net.ServerSocket;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import bham.bioshock.Config;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.utils.Clock;
import bham.bioshock.communication.server.CommunicationMaker;
import bham.bioshock.communication.server.StreamFactory;
import bham.bioshock.server.interfaces.StoppableServer;

@Singleton
public class Server implements StoppableServer {
  private ServerHandler handler;
  private CommunicationMaker connMaker;
  private ServerSocket serverSocket;
  private Store store;

  @Inject
  public Server(Store store) {
    this.store = store;
  }

  public Boolean start(String hostName) {
    Clock clock = new Clock();
    this.handler = new ServerHandler(store, Config.DEBUG_SERVER, clock);
    try {
      serverSocket = new ServerSocket(Config.PORT);
    } catch (IOException e1) {
      return false;
    }
    connMaker = new CommunicationMaker(new StreamFactory());
    connMaker.setHostName(hostName);
    connMaker.startSearch(handler, serverSocket, true);

    return true;
  }

  public void stop() {
    if(connMaker != null) {
      connMaker.disconnect();      
    }
    if(handler != null) {
      handler.abort();      
    }
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (IOException e) {
      }
    }
  }
}
