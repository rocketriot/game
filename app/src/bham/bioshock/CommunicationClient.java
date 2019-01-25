package bham.bioshock;

import bham.bioshock.communication.client.ClientConnectThread;
import bham.bioshock.communication.client.ClientService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommunicationClient {

	private static final String HOSTNAME = "localhost";
	private static final int PORT = 4444;

	public static void connect() {
		// Initialise information:
		String hostname = HOSTNAME;

		// Open sockets:
		ObjectOutputStream toServer = null;
		ObjectInputStream fromServer = null;
		Socket server = null;

		try {
			server = new Socket(hostname, PORT);
			toServer = new ObjectOutputStream(server.getOutputStream());
			fromServer = new ObjectInputStream(server.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + hostname);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("The server doesn't seem to be running " + e.getMessage());
			System.exit(1);
		}

		// We are connected to the server, create a service to get and send messages
		ClientService service = new ClientService(server, fromServer, toServer);
		service.start();
	}

	public static void main(String[] args) {
		ClientConnectThread cct = new ClientConnectThread();
		Thread discoveryThread = new Thread(cct);
	    discoveryThread.start();
	    try {
	    	discoveryThread.join();	    	
	    } catch (InterruptedException e) {
	    	System.err.println("Discovery interrupted");
	    }
	    System.out.println("Discovery finished");
	}
}