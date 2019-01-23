package bham.bioshock.communication.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;

/**
 * 
 * Reads from user console, checks if the provided command is valid i.e. if is
 * known and if user is allowed to use it (is logged in / logged out)
 */
public class ClientSender extends Thread {

	private ObjectOutputStream toServer;
	private ClientReceiver receiver;
	private ClientService service;
	private int number;

	ClientSender(ObjectOutputStream _toServer, ClientReceiver _receiver, ClientService _service) {
		toServer = _toServer;
		receiver = _receiver;
		service = _service;
		number = 0;
	}

	/**
	 * Start ClientSender thread.
	 */
	public void run() {
		try {
			
			while (true) {
				// Get informations about comment provided
				String commandName = String.valueOf(number++);
				// Command command = new Command();
				
				// Change the state before sending to the command to the server
//				service.executeBeforeSend(command);

				// Prepare list of arguments
//				ArrayList<String> arguments = new ArrayList<String>();
//				for (int i = 0; i < 1; i++) {
//				
//					String text = userInput.readLine();
//					arguments.add(text);
//				}

				// create new action with arguments
				Action action = new Action(commandName);
				System.out.println("Sending " + commandName);
				// send an action to the server
				toServer.writeObject(action);
				
				Thread.sleep(1000);
			}
		} catch (IOException e) {
			System.err.println("Communication broke in ClientSender" + e.getMessage());
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Client sender thread ending");
	}

}
