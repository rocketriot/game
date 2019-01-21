package bham.bioshock.communication;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Used to communication between client an server is sent by ClientSender or
 * ServerSender must contain a command, but can also a message and/or a list of
 * arguments
 * 
 * @author Jan Dabrowski
 *
 */
public class Action implements Serializable {

	private static final long serialVersionUID = 4181711659883987367L;

	/**
	 * Type of action
	 */
	private String command;

	/**
	 * Arguments for action
	 */
	private ArrayList<String> arguments;

	/**
	 * Message to be send through socket
	 */
	private Message message;

	/**
	 * Creates new action with arguments and a message to be send through the socket
	 * 
	 * @param _command
	 *            to be sent through socket
	 * @param _arguments
	 */
	public Action(ActionCommand _command, ArrayList<String> _arguments, Message _message) {
		command = _command.getText();
		arguments = _arguments;
		message = _message;
	}
	
	public Action(String _command) {
		command = _command;
		arguments = null;
		message = null;
	}

	/**
	 * Creates new action with arguments to be send through the socket
	 * 
	 * @param _command
	 * @param _arguments
	 */
	public Action(ActionCommand _command, ArrayList<String> _arguments) {
		this(_command, _arguments, null);
	}

	/**
	 * Creates new action with message to be send through socket
	 * 
	 * @param _command
	 *            name of the command
	 * @param _message
	 *            to be send
	 */
	public Action(ActionCommand _command, Message _message) {
		this(_command, null, _message);
	}

	/**
	 * Creates new action with one argument to be send through the socket
	 * 
	 * @param _command
	 * @param _argument
	 *            that will be added to list and sent
	 */
	public Action(ActionCommand _command, String _argument) {
		command = _command.getText();
		ArrayList<String> args = new ArrayList<String>();
		args.add(_argument);
		arguments = args;
	}

	/**
	 * Creates new action with a command only to be send through socket
	 * 
	 * @param _command
	 *            command to be sent
	 */
	public Action(ActionCommand _command) {
		this(_command, null, null);
	}

	/**
	 * Gets the name of the command
	 * 
	 * @return name of the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Gets the message
	 * 
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * List of arguments sent with the command
	 * 
	 * @return list of arguments
	 */
	public ArrayList<String> getArguments() {
		if (arguments == null) {
			return new ArrayList<String>();
		}
		return arguments;
	}
}
