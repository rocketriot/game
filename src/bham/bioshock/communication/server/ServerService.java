package bham.bioshock.communication.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import bham.bioshock.communication.Action;

/**
 * Executes the actions received by ServerReceiver.
 * 
 * @author Jan Dabrowski
 */
public class ServerService extends Thread {

	private ServerSender sender;
	private ServerReceiver receiver;

	private ServerConnectionDescription connection;
	private ArrayList<ServerService> clientConnections;


	public ServerService(
		ObjectInputStream fromClient,
		ObjectOutputStream toClient,
		ArrayList<ServerService> clientConnections
		)
	{

		// Sender and receiver for sending and receiving messages to/from user
		this.sender = new ServerSender(toClient);

		// Receiver for getting messages from user
		this.receiver = new ServerReceiver(this, fromClient);

		// Creates new description of the connection, this is stored in the user object
		// for handling multiple computers connected as the same user
		this.connection = new ServerConnectionDescription(this);
		
		this.clientConnections = clientConnections;
	}

	public void run() {
		// start the receiver thread
		receiver.start();

		try {
			// wait for the receiver to end
			receiver.join(); // end if receiver ends
		} catch (InterruptedException e) {
			// This shouldn't actually happen
			System.err.println("ServerService was interrupted");
		}
		
		System.out.println("ServerService ending");
	}

	public void send(String message) {
		sender.send(new Action(message));
	}
	
	/**
	 * Delegates the execution to the appropriate method
	 * 
	 * @param action
	 *            to be executed
	 */
	public void execute(Action action) {
		String command = action.getCommand();
		System.out.println("Received:" + command);
		
		for(ServerService ss : clientConnections) {
			ss.send(command);
		}
		
		// command received in the action
		// ClientCommand command = ClientCommand.findCommand(action.getCommand());
		
		// try {
		// 	// check if the number of arguments is correct
		// 	checkArgumentNumber(command, action);
		// 	// check if the login state is correct
		// 	checkLoginState(command);

		// 	// run appropriate method
		// 	switch (command) {
		// 	case LOGIN:
		// 		login(action);
		// 		break;
		// 	case REGISTER:
		// 		register(action);
		// 		break;
		// 	case LOGOUT:
		// 		logout(action);
		// 		break;
		// 	case PREVIOUS:
		// 		previous(action);
		// 		break;
		// 	case NEXT:
		// 		next(action);
		// 		break;
		// 	case CURRENT:
		// 		current(action);
		// 		break;
		// 	case DELETE:
		// 		delete(action);
		// 		break;
		// 	case QUIT:
		// 		quit();
		// 		break;
		// 	case SEND:
		// 		send(action);
		// 		break;
		// 	default:
		// 		break;
		// 	}

		// } catch (WrongArgumentsException e) {
		// 	Report.error("Error in action execution " + e.getMessage());
		// } catch (WrongCommandException e) {
		// 	// If we got wrong command from the client, send an error message
		// 	sender.send(new Action(ServerCommand.INVALID_COMMAND, e.getMessage()));
		// 	// and report an error
		// 	Report.error("Invalid command sent by client " + e.getMessage());
		// }
	}

	private void quit() {
		connection.logout();
	}
}
