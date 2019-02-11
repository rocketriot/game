package bham.bioshock.communication.client;

import bham.bioshock.client.Client;
import bham.bioshock.communication.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommunicationClient {

  public static InetAddress hostAddress;
  public static int port = Config.PORT;

  public static ClientService createConnection(Client client) throws ConnectException {
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
    ClientService service = new ClientService(server, fromServer, toServer, client);
    service.start();
    return service;
  }

  public static void setHostAddress(InetAddress address) {
    hostAddress = address;
  }

  public static ClientService connect(String userName, Client client) throws ConnectException {
    Thread discoveryThread = new Thread(new ClientConnectThread(userName));
    discoveryThread.start();

    try {
      discoveryThread.join();
      return createConnection(client);
    } catch (InterruptedException e) {
      System.err.println("Connection interrupted");
    } catch (ConnectException e) {
    }
    throw new ConnectException("Connection unsuccessful");
  }

  public static void main(String[] args) {
    try {
      CommunicationClient.connect("Test", null);
    } catch (ConnectException e) {
      e.printStackTrace();
    }
  }
}
