package bham.bioshock.server.interfaces;

public interface StoppableServer {

  /**
   * Stop threads discovering new clients
   */
  public void stopDiscovery();
  
  /**
   * Stop the server
   */
  public void stop();
}
