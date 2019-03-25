package bham.bioshock.communication.interfaces;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.Socket;


public interface ObjectStreamFactory {

  /**
   * Creates new output stream
   * 
   * @param socket
   * @return
   * @throws IOException
   */
  ObjectOutput getOutput(Socket socket) throws IOException;
  
  /**
   * Creates new input stream
   * 
   * @param socket
   * @return
   * @throws IOException
   */
  ObjectInput getInput(Socket socket) throws IOException;

}
