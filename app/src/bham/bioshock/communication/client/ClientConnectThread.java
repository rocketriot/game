package bham.bioshock.communication.client;

import bham.bioshock.Config;
import bham.bioshock.communication.Command;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientConnectThread extends Thread {

  private static final Logger logger = LogManager.getLogger(ClientConnectThread.class);
  
  public ClientConnectThread() {
    super("ClientConnectThread");
  }

  /**
   * Send UDP packet to provided address
   * 
   * @param c UDP socket
   * @param data bytes to be sent
   * @param address
   */
  private void sendPacket(DatagramSocket c, byte[] data, InetAddress address) {
    try {
      DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, Config.PORT);
      c.send(sendPacket);
    } catch (IOException e) {
      logger.error("UDP discovery packet sending error " + e.getMessage());
    }
  }

  @Override
  public void run() {
    DatagramSocket c = null;
    try {
      c = new DatagramSocket();
      c.setBroadcast(true);
      byte[] data = Command.COMM_DISCOVER_REQ.getBytes();
      sendPacket(c, data, InetAddress.getByName("255.255.255.255"));

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
          sendPacket(c, data, broadcast);
           
          logger.info("Discovery packet sent to " + broadcast.getHostAddress() + " " + networkInterface.getDisplayName());
        }
      }

      // Wait for a response
      byte[] buffer = new byte[Command.COMM_DISCOVER_RES.getBytes().length];
      DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
      c.receive(receivePacket);

      // Check if the message is correct
      String message = new String(receivePacket.getData()).trim();
      if (message.equals(Command.COMM_DISCOVER_RES.toString())) {
        // Save host address in the communication client class
        CommunicationClient.setHostAddress(receivePacket.getAddress().getHostAddress());
      }

    } catch (IOException ex) {
      logger.catching(ex);
    } finally {
      if (c != null) {
        c.close();
      }
    }
  }
}
