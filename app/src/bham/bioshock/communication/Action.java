package bham.bioshock.communication;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

/**
 * Used to communication between client an server is sent by ClientSender or
 * ServerSender must contain a command, but can also a message and/or a list of
 * arguments
 * 
 */
public class Action implements Serializable, Comparable<Action> {

	private static final long serialVersionUID = 4181711659883987367L;

	/**
	 * Type of action
	 */
	private Command command;

	/**
	 * Arguments for action
	 */
	private ArrayList<String> arguments;
	
	private long created;

	/**
	 * Creates new action with arguments and a message to be send through the socket
	 * 
	 * @param _command to be sent through socket
	 * @param _arguments
	 */
	public Action(Command _command, ArrayList<String> _arguments) {
		command = _command;
		arguments = _arguments;
		created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
	}
	
	public Action(Command _command) {
		this(_command, null);
	}

	/**
	 * Gets the name of the command
	 * 
	 * @return name of the command
	 */
	public Command getCommand() {
		return command;
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

	@Override
	public int compareTo(Action o) {
		long diff = this.created - o.created;
		return diff > 0 ? -1 : 1;
	}
}
