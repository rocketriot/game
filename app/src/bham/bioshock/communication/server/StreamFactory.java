package bham.bioshock.communication.server;

import bham.bioshock.communication.interfaces.ObjectStreamFactory;

import java.io.*;
import java.net.Socket;

public class StreamFactory implements ObjectStreamFactory {

  /* (non-Javadoc)
   * @see bham.bioshock.communication.server.ObjectStreamFactory#getOutput(java.net.Socket)
   */
  @Override
  public ObjectOutput getOutput(Socket socket) throws IOException {
    return new ObjectOutputStream(socket.getOutputStream());
  }

  /* (non-Javadoc)
   * @see bham.bioshock.communication.server.ObjectStreamFactory#getInput(java.net.Socket)
   */
  @Override
  public ObjectInput getInput(Socket socket) throws IOException {
    return new ObjectInputStream(socket.getInputStream());
  }
}
