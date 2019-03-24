package bham.bioshock.testutils.communication;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class FakeMessage extends Message {

  private static final long serialVersionUID = -786355558500833719L;

  public FakeMessage() {
    super(Command.TEST);
  }

}
