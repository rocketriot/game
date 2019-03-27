package bham.bioshock.communication.client;

import bham.bioshock.Config;
import bham.bioshock.common.models.store.CommunicationStore;
import bham.bioshock.communication.Command;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientConnectThread extends Thread {

  private static final Logger logger = LogManager.getLogger(ClientConnectThread.class);
  
  private CommunicationStore store;
  private HashMap<String, Long> keepAlive = new HashMap<>();
  private DatagramSocket socket;
  private Thread receiverThread;
  
  public ClientConnectThread(CommunicationStore store) {
    super("ClientConnectThread");
    this.store = store;
  }

  /**
   * Send UDP packet to provided address
   * 
   * @param c UDP socket
   * @param data bytes to be sent
   * @param address
   */
  private void sendPacket(byte[] data, InetAddress address) {
    try {
      DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, Config.PORT);
      socket.send(sendPacket);
    } catch (IOException e) {
      logger.error("UDP discovery packet sending error " + e.getMessage());
    }
  }
  
  /**
   * Check if server responded in last 2 seconds
   */
  public void checkKeepAlive() {
    ArrayList<ServerStatus> servers = new ArrayList<>(store.getServers());
    
    for(ServerStatus s : servers) {
      Long time = keepAlive.get(s.getIP());
      long now = System.currentTimeMillis();
      
      if(time == null || now - time > 2 * 1000) {
        keepAlive.remove(s.getIP());
        store.unregister(s.getIP());
      }      
    }
  }

  /**
   * Try to find the server
   */
  @Override
  public void run() {    
    try {
      socket = new DatagramSocket();
      socket.setSoTimeout(1000);
      socket.setBroadcast(true);
      
      receiverThread = new Thread(new ConnectReceiver(), "ClientConnectReceiver");
      receiverThread.start();
      
      while(!isInterrupted()) {
        byte[] data = Command.COMM_DISCOVER_REQ.getBytes();
        sendPacket(data, InetAddress.getByName("255.255.255.255"));
  
        // Broadcast the message over all the network interfaces
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
          NetworkInterface networkInterface = interfaces.nextElement();
          
          // Ignore loopback and not running interfaces
          if (networkInterface.isLoopback() || !networkInterface.isUp())
            continue;
  
          // Get interface addresses
          for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            InetAddress broadcast = interfaceAddress.getBroadcast();
  
            if (broadcast == null)
              continue;
  
            // Send the broadcast package
            sendPacket(data, broadcast);
             
            logger.debug("Discovery packet sent to " + broadcast.getHostAddress() + " " + networkInterface.getDisplayName());
          }
        }
        sleep(500);
      }

    } catch (IOException ex) {
      logger.catching(ex);
    } catch (InterruptedException e) {
      logger.debug("ClientConnectThread interrupted");
    } finally {
      if(receiverThread != null) {
        receiverThread.interrupt();
      }
      if (socket != null) {
        socket.close();
      }
    }
    try {
      receiverThread.join();
    } catch(InterruptedException e) {
      logger.error("Unexpected interrupted");
    }
  }
  
  
  class ConnectReceiver implements Runnable {

    @Override
    public void run() {
      try {
        while(!isInterrupted()) { 
          // Wait for a response
          byte[] buffer = new byte[1000];
          DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
          
          try {
            socket.receive(receivePacket);
          
            // Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            
            if (message.startsWith(Command.COMM_DISCOVER_RES.toString())) {
              String response = message.replaceFirst(Command.COMM_DISCOVER_RES.toString(), "");
              String name = response.split(";")[0];
              String serverId = response.split(";")[1];
              
              // Save host address
              String ipAddress = receivePacket.getAddress().getHostAddress();
              ServerStatus server = new ServerStatus(name, ipAddress, serverId);
              store.register(server);
              keepAlive.put(server.getIP(), System.currentTimeMillis());
            }
          } catch(SocketTimeoutException e) {}
          
          checkKeepAlive();
        }
      } catch(IOException e) {
        logger.error("ClientConnectReceiver error " + e.getMessage());
      }
      logger.info("ClientConnectReceiver finished");
    }
    
  }
}
