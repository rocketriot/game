package bham.bioshock.communication.server;

import bham.bioshock.Config;
import bham.bioshock.communication.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.UUID;

public class DiscoveryThread extends Thread {

  private static final Logger logger = LogManager.getLogger(DiscoveryThread.class);

  UUID serverId;
  String name = "Server";

  public DiscoveryThread(String hostName, UUID serverId) {
    super("ServerDiscoveryThread");
    this.name = hostName;
    this.serverId = serverId;
  }

  /** Wait for a request packet from the client and respond */
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

            String response = Command.COMM_DISCOVER_RES.toString() + name + ";" + serverId;
            byte[] sendData = response.getBytes();
            // Send a response
            DatagramPacket sendPacket =
                new DatagramPacket(
                    sendData, sendData.length, packet.getAddress(), packet.getPort());
            socket.send(sendPacket);
          }
        } catch (SocketTimeoutException e) {
        }
        sleep(500);
      }
    } catch (IOException ex) {
      logger.catching(ex);
    } catch (InterruptedException e) {

    } finally {
      if (socket != null) {
        socket.close();
      }
      logger.debug("Discovery socket closed");
    }
  }
}
