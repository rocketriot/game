package bham.bioshock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import bham.bioshock.communication.server.ServerService;

public class Server {

	private static final int PORT = 4444;
	
	private static ArrayList<ServerService> clientConnections = new ArrayList<>();
	
	private static void createNewConnection(Socket socket) throws IOException {
		// Create streams for input and output
		ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
		ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

		// Service to execute business logic
		ServerService service = new ServerService(fromClient, toClient, clientConnections);
		clientConnections.add(service);
		service.start();
	}
	
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("Couldn't listen on port " + PORT);
			System.exit(1);
		}
		System.out.println("Server started!");
		
		try {
			while (true) {
				// Listen to the socket, accepting connections from new clients:
				Socket socket = serverSocket.accept();

				// Create streams and objects for sending messages to and from client
				createNewConnection(socket);
			}
		} catch (IOException e) {
			System.err.println("IO error " + e.getMessage());
		}
	}

}