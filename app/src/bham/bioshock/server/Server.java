package bham.bioshock.server;

import com.google.inject.Singleton;
import bham.bioshock.communication.server.CommunicationServer;
import bham.bioshock.communication.server.ServerHandler;

@Singleton
public class Server extends Thread {
  private ServerHandler handler;

  public Server() {
    this.handler = new ServerHandler();
  }

  public void run() {
    CommunicationServer.start(handler);
  }
}
