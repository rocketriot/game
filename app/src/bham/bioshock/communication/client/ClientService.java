package bham.bioshock.communication.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;

import bham.bioshock.communication.Action;

/**
 * Interprets commands received from server
 */
public class ClientService extends Thread {

	private ClientSender sender;
	private ClientReceiver receiver;
	private Socket server;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	private PriorityBlockingQueue<Action> queue = new PriorityBlockingQueue<>();

	/**
	 * Creates the service and helper objects to send and receive messages
	 * 
	 * @param _server
	 *            socket
	 * @param fromServer
	 *            stream from server
	 * @param toServer
	 *            stream to server
	 */
	public ClientService(Socket _server, ObjectInputStream _fromServer, ObjectOutputStream _toServer) {

		// save socket and streams for communication
		server = _server;
		fromServer = _fromServer;
		toServer = _toServer;

		// Create two client object to send and receive messages
		receiver = new ClientReceiver(this, fromServer);
		sender = new ClientSender(toServer);
	}

	/**
	 * Starts the sender and receiver threads
	 */
	public void run() {
		// Run sender and receiver in parallel:
		sender.start();
		receiver.start();

		try {
			while(true) {
				// Execute action from a blocking queue
				execute(queue.take());
			}
		} catch(InterruptedException e) {
			System.err.println("Client service was interrupted");
		}
		
		// wait for the threads to terminate and close the streams
		close();
	}

	public void store(Action action) {
		queue.add(action);
	}
	
	/**
	 * Execute the action and change state if necessary
	 * 
	 * @param action
	 *            to be executed
	 */
	public void send(Action action) {
		sender.send(action);
	}

	private void execute(Action action) {
		System.out.println(action.getCommand().toString());
	}
	
	/**
	 * Wait for the threads to terminate and than close the sockets and streams
	 */
	public void close() {
		try {
			sender.join();
			System.out.println("Client sender ended");
			toServer.close();
			fromServer.close(); 
			server.close();
			receiver.join();
			System.out.println("Client receiver ended");
		} catch (IOException e) {
			System.err.println("Something wrong " + e.getMessage());
			System.exit(0);
		} catch (InterruptedException e) {
			System.err.println("Unexpected interruption " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Client ended. Goodbye.");
	}
}
