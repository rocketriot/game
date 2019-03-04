package bham.bioshock.communication.client;

import bham.bioshock.communication.Config;
import bham.bioshock.communication.server.CommunicationServer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.inject.Singleton;

@Singleton
public class CommunicationClient {

  private static final Logger logger = LogManager.getLogger(CommunicationClient.class);

  public static String hostAddress;
  public static int port = Config.PORT;
  private ClientService service = new ClientService();;

  public ClientService getConnection() {
    return service;
  }

  public ClientService createConnection() throws ConnectException {
    if (service.isCreated()) {
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
    service.create(server, fromServer, toServer);
    service.start();
    logger.debug("Client connected!");
    return service;
  }


  public ClientService connect(String userName) throws ConnectException {
    if (Config.SERVER_ADDRESS.length() == 0) {
      ClientConnectThread c = new ClientConnectThread();
      long waiting = 0;
      c.start();

      // Wait for 5 seconds for the ClientConnectThread
      try {
        while (c.isAlive()) {
          Thread.sleep(200);
          waiting += 200;
          
          if(waiting > 5000) {
            c.interrupt();
            throw new ConnectException("IP address not configured and no server has been found");
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    } else {
      CommunicationClient.hostAddress = Config.SERVER_ADDRESS;
    }

    try {
      return createConnection();
    } catch (ConnectException e) {
    }
    throw new ConnectException("Connection unsuccessful");
  }

  public static void setHostAddress(String address) {
    hostAddress = address;
  }
}
