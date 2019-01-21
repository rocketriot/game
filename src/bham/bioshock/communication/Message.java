package bham.bioshock.communication;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Message from user to user
 * 
 * @author Jan Dabrowski
 */
public class Message implements Serializable, Comparable<Message> {

	private static final long serialVersionUID = -4251585100956884457L;

	// used to generate future ids
	private static int nextId = 1;
	
	// id of the message
	private final int id;
	// sender username
	private final String sender;
	// receiver username
	private final String receiver;
	// content of the message
	private final String text;
	// time when the message was received
	private LocalDateTime time;
	
	/**
	 * Create a message with sender and content
	 * 
	 * @param sender
	 * @param text
	 */
	public Message(String sender, String receiver, String text) {
		this.id = (Message.nextId++);
		this.sender = sender;
		this.receiver = receiver;
		this.text = text;
	}
	
	/** 
	 * Create a message object from scratch
	 * 
	 * @param id of the message
	 * @param sender username
	 * @param receiver username
	 * @param text content of the message
	 * @param epoch time in unix format
	 */
	public Message(Integer id, String sender, String receiver, String text, long epoch) {
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.text = text;

		// Convert long to local date time
		this.time = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC);

		// adjust the class property
		if (nextId < id) {
			nextId = id + 1;
		}
	}

	/**
	 * Saved current time
	 */
	public void saveReceivedTime() {
		this.time = LocalDateTime.now();
	}
	
	/**
	 * @return the time when the message was received
	 */
	public LocalDateTime getTime() {
		return this.time;
	}
	
	/**
	 * @return sender username
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @return receiver username
	 */
	public String getReceiver() {
		return receiver;
	}
	
	/**
	 * @return content of the message
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return id of the message
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Create new message from the string
	 * 
	 * Message.fromString( myMessage.toString() ) 
	 * should return similar object to myMessage
	 * 
	 * @param line definition of the message
	 * @return new message object created from the definition
	 */
	public static Message fromString(String line) {
		String[] messageDesc = line.split(";");

		// This means that the message is correctly stored
		if (messageDesc.length == 5) {
			Integer id = Integer.parseInt(messageDesc[0]);
			String sender = messageDesc[1];
			String receiver = messageDesc[2];
			Long epoch = Long.parseLong(messageDesc[3])*1000;
			String content = messageDesc[4];

			return new Message(id, sender, receiver, content, epoch);
		}

		return null;
	}
	
	/**
	 * Convert the message to the string
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getId() + ";" + getSender() + ";" + getReceiver());
		sb.append(";" + getEpoch() + ";");
		sb.append(getText().replaceAll("\n", "\\n").replaceAll(";", ",") + "\n");
		return sb.toString();
	}
	
	/**
	 * @return received time in unix format
	 */
	private long getEpoch() {
		return getTime().toEpochSecond(ZoneOffset.UTC);
	}
	
	/** 
	 * Compare two message using their id
	 * 
	 * @param message to compare
	 * @return true if messages are the same
	 */
	public boolean equals(Message m) {
		return m.getId() == id;
	}
	
	/**
	 * Compare two messages according to the date when were received
	 */
	@Override
	public int compareTo(Message toCompare) {

		if (this.getEpoch() < toCompare.getEpoch()) {
			return -1;
		} else if (this.getEpoch() > toCompare.getEpoch()) {
			return 1;
		} else {
			return 0;
		}
	}

}
