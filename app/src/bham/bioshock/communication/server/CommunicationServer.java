package bham.bioshock.communication.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import bham.bioshock.communication.Config;
import bham.bioshock.server.Server;

public class CommunicationServer {
	private static ServerService createNewConnection(Socket socket, ServerHandler handler, Server server)
			throws IOException {
		// Create streams for input and output
		ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
		ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

		// Service to execute business logic
		ServerService service = new ServerService(fromClient, toClient, handler, server);
		service.start();
		return service;
	}

	public static Thread discoverClients() {
		Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
		discoveryThread.start();
		return discoveryThread;
	}

	public static void start(ServerHandler handler, Server server) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(Config.PORT);
		} catch (IOException e) {
			System.err.println("Couldn't listen on port " + Config.PORT);
		}
		System.out.println("Server started!");
		Thread discoveryThread = discoverClients();

		try {
			while (true) {
				Socket socket = serverSocket.accept();
				// Create streams and objects for sending messages to and from client
				ServerService service = createNewConnection(socket, handler, server);
				handler.register(service);
			}
		} catch (IOException e) {
			System.err.println("IO error " + e.getMessage());
		}
		discoveryThread.interrupt();
	}

	public static void main(String[] args) {
		start(new ServerHandler(), null);
	}

}