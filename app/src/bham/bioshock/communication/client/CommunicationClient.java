package bham.bioshock.communication.client;

import bham.bioshock.Config;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.CommunicationStore;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.inject.Singleton;

@Singleton
public class CommunicationClient {

  private static final Logger logger = LogManager.getLogger(CommunicationClient.class);

  public static int port = Config.PORT;
  private ClientService service = null;
  private ReconnectionThread reconnect = null;
  private ClientConnectThread discoverThread = null;
  private ServerStatus currentServer;
  
  
  /**
   * Get current connection to the server
   * 
   * @return Optional with connection 
   */
  public Optional<ClientService> getConnection() {
    if(service == null) {
      return Optional.empty();
    }
    return Optional.of(service);
  }

  /**
   * Starts reconnection thread if one is not already running
   * 
   * @param router
   */
  public void startReconnectionThread(Router router) {
    if(reconnect == null || !reconnect.isAlive()) {
      reconnect = new ReconnectionThread(this, router);
      reconnect.start();      
    }
  }
  
  /**
   * Creates new connection with the server
   * 
   * @return
   * @throws ConnectException
   */
  public ClientService createConnection(String hostAddress) throws ConnectException {
    if (service != null && service.isCreated()) {
      return service;
    }
    // Open sockets:
    ObjectOutputStream toServer = null;
    ObjectInputStream fromServer = null;
    Socket server = null;

    try {
      server = new Socket(hostAddress, port);
      toServer = new ObjectOutputStream(server.getOutputStream());
      fromServer = new ObjectInputStream(server.getInputStream());
    } catch (UnknownHostException e) {
      throw new ConnectException("Unknown host: " + hostAddress);
    } catch (IOException e) {
      throw new ConnectException("The server doesn't seem to be running " + e.getMessage());
    }

    // We are connected to the server, create a service to get and send messages
    service = new ClientService(server, fromServer, toServer);
    service.start();
    logger.debug("Client connected!");
    return service;
  }
  
  public void saveToFile(String name, UUID playerId) {
    if(currentServer == null) return;
    try (PrintStream out = new PrintStream(new FileOutputStream("app/hostInfo.txt"))) {
      out.print(playerId.toString() + "\n" + currentServer.getId());
      out.flush();
    } catch(FileNotFoundException e) {}
  }
  
  public ServerStatus fromFile() {
    BufferedReader reader = null;
    try {
      
      reader = new BufferedReader(new FileReader("app/hostInfo.txt"));
      String playerId = reader.readLine();
      String serverId = reader.readLine();
      
      ServerStatus server = new ServerStatus("?", "?", serverId);
      server.setPlayerId(UUID.fromString(playerId));
      return server;
      
    } catch (Exception e) {
    } finally {
      if(reader != null) {
        try {
          reader.close();
        } catch (IOException e) {}        
      }
    }
    return null;
  }
  
  public void discover(CommunicationStore store, Router router) {
    ServerStatus server = fromFile();
    if(server != null) {
      store.setRecoveredServer(server);
    }
    discoverThread = new ClientConnectThread(store);
    discoverThread.start();
  }
  
  public void stopDiscovery() {
    if(discoverThread != null) {
      discoverThread.interrupt();      
    }
  }
  
  public boolean reconnect(String ip) {
    if(currentServer == null && ip == null) {
      logger.fatal("Connection has been lost");
      return false;
    }
    
    String address = ip != null ? ip : currentServer.getIP();
    logger.info("Reconnecting to " + address);
    try {
      createConnection(address);
      logger.info("Reconnected!");
      return true;
    } catch (ConnectException e) {
      logger.catching(e);
    }
    return false;
  }

  public ClientService connect(ServerStatus server) throws ConnectException {
    this.currentServer = server;
    try {
      return createConnection(server.getIP());
    } catch (ConnectException e) {
    }
    throw new ConnectException("Connection unsuccessful");
  }

  public void disconnect() {
    this.currentServer = null;
    if(reconnect != null) {
      reconnect.interrupt();
      reconnect = null;
    }
    if(service != null) {
      service.abort();
    }
  }

}
