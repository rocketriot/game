package bham.bioshock.testutils.server;

import bham.bioshock.server.interfaces.StoppableServer;

public class FakeServer implements StoppableServer {

  public boolean discoveryRunning = true;
  public boolean running = true;
  
  @Override
  public void stopDiscovery() {
    this.discoveryRunning = false;
  }

  @Override
  public void stop() {
    this.discoveryRunning = false;
    this.running = false;
  }
  
  public void reset() {
    this.discoveryRunning = true;
    this.running = true;
  }

}
