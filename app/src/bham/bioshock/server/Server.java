package bham.bioshock.server;

import bham.bioshock.communication.server.CommunicationServer;
import bham.bioshock.communication.server.ServerHandler;
import com.google.inject.Singleton;

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
