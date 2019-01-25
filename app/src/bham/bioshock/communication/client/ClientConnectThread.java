package bham.bioshock.communication.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.Config;

public class ClientConnectThread implements Runnable {

	private String name;
	
	public ClientConnectThread(String name) {
		this.name = name;
	}
	
	private void sendPacket(DatagramSocket c, byte[] data, InetAddress address) {
		try {
			DatagramPacket sendPacket = new DatagramPacket(
				data, 
				data.length,
				address,
				Config.PORT
			);	
			c.send(sendPacket);				
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	@Override
	public void run() {
		DatagramSocket c;
		try {
			// Open a random port to send the package
			c = new DatagramSocket();
			c.setBroadcast(true);
			String broadcastMessage = Command.COMM_DISCOVER.toString() + ";" + this.name;
			byte[] data = broadcastMessage.getBytes(); 
			sendPacket(c, data, InetAddress.getByName("255.255.255.255"));
		
			// Broadcast the message over all the network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp())
					continue;

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					
					if (broadcast == null)
						continue;

					// Send the broadcast package
					sendPacket(c, data, broadcast);

					System.out.println(
						"Request packet sent to: " + broadcast.getHostAddress() + ";"
						+ "Interface: " + networkInterface.getDisplayName()
					);
				}
			}

			System.out.println("Waiting for a connection...");

			// Wait for a response
			byte[] buffer = new byte[15000];
			DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
			c.receive(receivePacket);

			// Check if the message is correct
			String message = new String(receivePacket.getData()).trim();
			if (message.equals(Command.COMM_DISCOVER_RESPONSE.toString())) {
				Client.setHostAddress(receivePacket.getAddress());
			}

			c.close();
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}
}
