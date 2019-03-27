package bham.bioshock.server.interfaces;

public interface StoppableServer {

  /**
   * Stop threads discovering new clients
   */
  void stopDiscovery();
  
  /**
   * Stop the server
   */
  void stop();
}
