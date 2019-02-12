package bham.bioshock.communication.client;

import bham.bioshock.communication.Config;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import com.google.inject.Singleton;

@Singleton
public class CommunicationClient {

  public static String hostAddress;
  public static int port = Config.PORT;
  private ClientService service = null;

  public ClientService getConnection() {
    return service;
  }

  public ClientService createConnection() throws ConnectException {
    if (service != null) {
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
    return service;
  }


  public ClientService connect(String userName) throws ConnectException {
    if(Config.SERVER_ADDRESS.length() == 0) {
      ClientConnectThread c = new ClientConnectThread();
      c.run();
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
