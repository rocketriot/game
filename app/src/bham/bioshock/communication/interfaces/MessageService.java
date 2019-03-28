package bham.bioshock.communication.interfaces;

import bham.bioshock.communication.messages.Message;

public interface MessageService {

  /**
   * Send a message
   *
   * @param message
   */
  void send(Message message);
}
