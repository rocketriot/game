package bham.bioshock.communication.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.Config;

public class CommunicationClient {
	
	public static InetAddress hostAddress;
	public static int port = Config.PORT; 

	public static ClientService createConnection() throws ConnectException {
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
		ClientService service = new ClientService(server, fromServer, toServer);
		service.start();
		return service;
	}
	
	public static void setHostAddress(InetAddress address) {
		hostAddress = address;
	}
	
	public static ClientService connect(String userName) throws ConnectException {
		Thread discoveryThread = new Thread(new ClientConnectThread(userName));
		discoveryThread.start();

	    try {
	    	discoveryThread.join();
	    	return createConnection();
	    } catch (InterruptedException e) {
	    	System.err.println("Connection interrupted");
	    } catch (ConnectException e) {}
	    throw new ConnectException("Connection unsuccessful");
	}

	public static void main(String[] args) {
		// Get name
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your username:");
		String name = sc.nextLine();
		ClientService service;
		
		try {
			service = connect(name);
			
			while(true) {
			    System.out.println("Enter command:");
			    String command = sc.nextLine();
			    ArrayList<String> arguments = new ArrayList<String>();
			    arguments.add(command);
			    service.send(new Action(Command.TEST, arguments));
			}
			
		} catch (ConnectException e) {
			System.out.println("Connection error");
		} finally {
			sc.close();			
		}
	}

}