package bham.bioshock.communication.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.server.ServerHandler;

public class CommunicationMaker extends Thread {
  
  private static final Logger logger = LogManager.getLogger(CommunicationMaker.class);
  
  private boolean connecting = false;
  private ServerSocket serverSocket = null;
  private ServerHandler handler;
  private Thread discoveryThread;
  
  public CommunicationMaker() {
    super("CommunicationMaker");
  }
  
  private PlayerService createNewConnection(Socket socket, ServerHandler handler)
      throws IOException {
    // Create streams for input and output
    ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
    ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

    // Service to execute business logic
    PlayerService service = new PlayerService(fromClient, toClient, handler);
    service.start();
    return service;
  }

  /**
   * Starts discovery thread which sends UDP packets to all
   * available interfaces
   */
  private void discoverClients() {
    discoveryThread = new Thread(new DiscoveryThread(), "DiscoveryThread");
    discoveryThread.start();
  }
  
  public boolean socketCreated() {
    return serverSocket != null;
  }
  
  public void startSearch(ServerHandler handler, ServerSocket serverSocket) {
    this.handler = handler;
    this.serverSocket = serverSocket;
    connecting = true;
    
    discoverClients();
    start();
  }
  
  /**
   * Establishing new connection through a socket
   */
  public void run() {
    logger.debug("Server started!");
    try {
      serverSocket.setSoTimeout(2000);
      // Register new clients
      while (connecting) {
        try {
          Socket socket = serverSocket.accept();
          // Create streams and objects for sending messages to and from client
          PlayerService service = createNewConnection(socket, handler);
          handler.register(service);
        } catch(SocketTimeoutException e) {}
      }
    } catch (IOException e) {
      logger.error("IO error " + e.getMessage());
    } finally {
      close();   
    } 
    logger.debug("Search finished");
  }
  
  /**
   * Abort client discovery process
   */
  public void stopDiscovery() {
    discoveryThread.interrupt();
  }
  
  /**
   * Stop the server
   */
  public void disconnect() {
    this.connecting = false;
  }
  
  /**
   * Close all used sockets
   */
  private void close() {
    // Stop related discovery thread
    if(discoveryThread != null) {
      discoveryThread.interrupt();         
    }  
    
    // Close the socket
    if(serverSocket == null) return;
    try {
      serverSocket.close();
    } catch(IOException e) {
      logger.error("Error while closing discovery socket: " + e.getMessage());
    } finally {
      serverSocket = null;
    }
  }

}
