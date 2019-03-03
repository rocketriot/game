package bham.bioshock.communication.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommunicationMaker extends Thread {
  
  private static final Logger logger = LogManager.getLogger(CommunicationMaker.class);
  private boolean connecting = false;
  private ServerSocket serverSocket = null;
  private ServerHandler handler;
  private Thread discoveryThread;
  
  private ServerService createNewConnection(Socket socket, ServerHandler handler)
      throws IOException {
    // Create streams for input and output
    ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
    ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

    // Service to execute business logic
    ServerService service = new ServerService(fromClient, toClient, handler);
    service.start();
    return service;
  }

  public void discoverClients() {
    discoveryThread = new Thread(new DiscoveryThread());
    discoveryThread.start();
  }
  
  public boolean socketCreated() {
    return serverSocket != null;
  }
  
  public void run() {
    logger.debug("Server started!");
    try {
      serverSocket.setSoTimeout(2000);
      // Register new clients
      while (connecting) {
        try {
          Socket socket = serverSocket.accept();
          // Create streams and objects for sending messages to and from client
          ServerService service = createNewConnection(socket, handler);
          handler.register(service);
        } catch(SocketTimeoutException e) {
          
        }
      }
    } catch (IOException e) {
      logger.error("IO error " + e.getMessage());
      close();
    } finally {
      // Stop the discovery thread
      discoveryThread.interrupt();      
    } 
    logger.debug("Search finished");
  }

  public void startSearch(ServerHandler handler, ServerSocket serverSocket) {
    connecting = true;
    this.handler = handler;
    this.serverSocket = serverSocket;
    
    this.discoverClients();
    this.start();
  }
  
  public void stopDiscovery() {
    connecting = false; 
  }
  
  public void close() {
    if(serverSocket == null) return;
    try {
      serverSocket.close();
    } catch(IOException e) {
      logger.error(e.getMessage());
    } finally {
      serverSocket = null;
    }
  }

}
