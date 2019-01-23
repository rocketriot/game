package bham.bioshock.communication.server;

/**
 * 
 * Description of the connection for the particular client
 * 
 * @author Jan Dabrowski
 *
 */
public class ServerConnectionDescription {
	private ServerService service;
	private int messageNum;
	private boolean loggedIn;

	public ServerConnectionDescription(ServerService _service) {
		service = _service;
		messageNum = 0;
		loggedIn = false;
	}

	/**
	 * Increase next message number
	 */
	public void next() {
		messageNum -= 1;
	}

	/**
	 * Decrease the message number
	 */
	public void previous() {
		messageNum += 1;
	}

	/**
	 * Take previous message number
	 */
	public int getPrevious() {
		previous();
		return messageNum;
	}

	/**
	 * Take next message number
	 */
	public int getNext() {
		next();
		return messageNum;
	}

	/**
	 * Adjusts message number after the message at the specified index was deleted
	 * 
	 * eg. user is reading message number 3, but message number 1 was deleted. This
	 * means the user reads currently the message number 2
	 * 
	 * @param deletedNum
	 */
	public void messageDeletedAt(int deletedNum) {
		if (messageNum > deletedNum) {
			messageNum -= 1;
		}
	}

	/**
	 * 
	 * @return the number of the message
	 */
	public int getMessageNum() {
		return messageNum;
	}

	/**
	 * @return sets the number of the message
	 */
	public void setMessageNum(int num) {
		messageNum = num;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void login() {
		loggedIn = true;
	}

	public void logout() {
		loggedIn = false;
	}
}
