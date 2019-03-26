package bham.bioshock.testutils.communication.streams;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.Socket;
import bham.bioshock.communication.interfaces.ObjectStreamFactory;

public class FakeStreamFactory implements ObjectStreamFactory {

  FakeObjectOutput output;
  FakeObjectInput input;
  
  public FakeStreamFactory(FakeObjectOutput output, FakeObjectInput input) {
    this.output = output;
    this.input = input;
  }
  
  @Override
  public ObjectOutput getOutput(Socket socket) throws IOException {
    return new FakeObjectOutput();
  }

  @Override
  public ObjectInput getInput(Socket socket) throws IOException {
    return new FakeObjectInput();
  }

}
