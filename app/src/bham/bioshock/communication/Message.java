package bham.bioshock.communication;

import java.io.Serializable;

/** Message from user to user */
public class Message implements Serializable {

  private static final long serialVersionUID = -4251585100956884457L;

  // used to generate future ids
  private static int nextId = 1;

  // id of the message
  private final int id;
  // content of the message
  private final String text;

  public Message(String text) {
    this.id = (Message.nextId++);
    this.text = text;
  }
  /** @return content of the message */
  public String getText() {
    return text;
  }

  /** @return id of the message */
  public int getId() {
    return id;
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
}
