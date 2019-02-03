package bham.bioshock.communication.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.server.Server;

/**
 * Executes the actions received by ServerReceiver.
 */
public class ServerService extends Thread {

	private ServerSender sender;
	private ServerReceiver receiver;
	private PriorityBlockingQueue<Action> queue = new PriorityBlockingQueue<>();
	private ServerHandler handler;
	private Server server;

	public ServerService(ObjectInputStream fromClient, ObjectOutputStream toClient, ServerHandler handler,
			Server server) {
		// Sender and receiver for sending and receiving messages to/from user
		this.sender = new ServerSender(toClient);

		// Receiver for getting messages from user
		this.receiver = new ServerReceiver(this, fromClient);

		this.handler = handler;

		this.server = server;
	}

	public void run() {
		// start the receiver thread
		receiver.start();

		try {
			while (true) {
				// Execute actions from queue
				execute(queue.take());
			}
			// wait for the receiver to end
		} catch (InterruptedException e) {
			receiver.interrupt(); // end if receiver ends
			// This shouldn't actually happen
			System.err.println("ServerService was interrupted");
		}

		System.out.println("ServerService ending");
	}

	public void store(Action action) {
		queue.add(action);
	}

	public void send(Action action) {
		sender.send(action);
	}

	public void sendToAll(Action action) {
		handler.sendToAll(action);
	}

	/**
	 * Delegates the execution to the appropriate method
	 * 
	 * @param action to be executed
	 */
	private void execute(Action action) {
		System.out.println(server);
		if (server != null) {
			server.handleRequest(action, this);
		}

		Command command = action.getCommand();
		ArrayList<String> arguments = action.getArguments();
		System.out.println(command);
		for (String a : arguments) {
			System.out.print(a + "; ");
		}
		System.out.print("\n");
	}
}
