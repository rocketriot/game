package bham.bioshock.communication.server;

import bham.bioshock.communication.interfaces.ObjectStreamFactory;
import bham.bioshock.communication.interfaces.ServerService;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

public class CommunicationMaker extends Thread {

  private static final Logger logger = LogManager.getLogger(CommunicationMaker.class);

  private boolean connecting = false;
  private ServerSocket serverSocket = null;
  private MultipleConnectionsHandler handler;
  private Thread discoveryThread;
  private ObjectStreamFactory streamFactory;
  private String hostName;

  public CommunicationMaker(ObjectStreamFactory streamFactory) {
    super("CommunicationMaker");
    this.streamFactory = streamFactory;
  }

  private ServerService createNewConnection(Socket socket, MultipleConnectionsHandler handler)
      throws IOException {
    // Create streams for input and output
    ObjectInput fromClient = streamFactory.getInput(socket);
    ObjectOutput toClient = streamFactory.getOutput(socket);

    // Service to execute business logic
    ServerService service = new PlayerService(fromClient, toClient, handler);
    service.start();
    return service;
  }

  public void setHostName(String name) {
    this.hostName = name;
  }

  /** Starts discovery thread which sends UDP packets to all available interfaces */
  private void discoverClients(UUID serverId) {
    discoveryThread = new DiscoveryThread(hostName, serverId);
    discoveryThread.start();
  }

  /**
   * Start connecting, for all new connected clients new streams will be created and ServerService
   * will be added to the handler
   *
   * @param handler
   * @param serverSocket
   * @param discoverClients
   */
  public void startSearch(
      MultipleConnectionsHandler handler,
      ServerSocket serverSocket,
      UUID serverId,
      boolean discoverClients) {
    this.handler = handler;
    this.serverSocket = serverSocket;
    connecting = true;

    if (discoverClients) {
      discoverClients(serverId);
    }

    start();
  }

  /** Establishing new connection through a socket */
  public void run() {
    logger.debug("Server started!");
    if (serverSocket == null) {
      logger.fatal("CommunicationMaker started without a socket! Use startSearch() method instead");
      return;
    }

    try {
      serverSocket.setSoTimeout(1000);
      // Register new clients
      while (connecting) {
        try {
          Socket socket = serverSocket.accept();
          // Create streams and objects for sending messages to and from client
          ServerService service = createNewConnection(socket, handler);
          handler.add(service);
        } catch (SocketTimeoutException e) {
        }
      }
    } catch (IOException e) {
      logger.catching(e);
    } finally {
      logger.debug("Search finished. Reconnection will be impossible");
      close();
    }
  }

  /** Stop the server */
  public void disconnect() {
    this.connecting = false;
  }

  /**
   * Checks if all related threads are stopped
   *
   * @return true if all related threads stopped
   */
  public boolean aborted() {
    return !isAlive() && !discoveryThread.isAlive();
  }

  /** Close all used sockets */
  private void close() {
    // Stop related discovery thread
    if (discoveryThread != null) {
      discoveryThread.interrupt();
    }

    // Close the socket
    if (serverSocket == null) return;
    try {
      serverSocket.close();
    } catch (IOException e) {
      logger.error("Error while closing discovery socket: " + e.getMessage());
    } finally {
      serverSocket = null;
    }
  }
}
