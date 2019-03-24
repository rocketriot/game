package bham.bioshock.communication.server;

import bham.bioshock.Config;
import bham.bioshock.communication.Command;
import java.io.IOException;
import java.net.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DiscoveryThread implements Runnable {
  
  private static final Logger logger = LogManager.getLogger(DiscoveryThread.class);
  
  /**
   * Wait for a request packet from the client and respond
   */
  @Override
  public void run() {
    DatagramSocket socket = null;
    try {
      socket = new DatagramSocket(Config.PORT, InetAddress.getByName("0.0.0.0"));
      socket.setBroadcast(true);
      socket.setSoTimeout(2000);

      while (!Thread.currentThread().isInterrupted()) {
        byte[] buffer = new byte[Command.COMM_DISCOVER_REQ.getBytes().length];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
          socket.receive(packet);

          String message = new String(packet.getData()).trim();
          if (message.equals(Command.COMM_DISCOVER_REQ.toString())) {

            byte[] sendData = Command.COMM_DISCOVER_RES.getBytes();
            // Send a response
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                packet.getAddress(), packet.getPort());
            socket.send(sendPacket);
          }
        } catch (SocketTimeoutException e) {
        }
      }
    } catch (IOException ex) {
      logger.catching(ex);
    } finally {
      if(socket != null) {
        socket.close();
      }
      logger.debug("Discovery socket closed");
    }
  }

}
