package bham.bioshock.communication.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.Config;

public class DiscoveryThread implements Runnable {

	@Override
	public void run() {
		DatagramSocket socket;
		try {
			socket = new DatagramSocket(Config.PORT, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (true) {
				byte[] buffer = new byte[15000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				String message = new String(packet.getData()).trim();
				String[] values = message.split(";");
				if (values[0].equals(Command.COMM_DISCOVER.toString())) {
					System.out.println(values[1] + " connected");
					byte[] sendData = Command.COMM_DISCOVER_RESPONSE.getBytes();

					// Send a response
					DatagramPacket sendPacket = new DatagramPacket(
						sendData, 
						sendData.length, 
						packet.getAddress(),
						packet.getPort()
					);
					socket.send(sendPacket);
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(DiscoveryThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static DiscoveryThread getInstance() {
		return DiscoveryThreadHolder.INSTANCE;
	}

	private static class DiscoveryThreadHolder {
		private static final DiscoveryThread INSTANCE = new DiscoveryThread();
	}

}