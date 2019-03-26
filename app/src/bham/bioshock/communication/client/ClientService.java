package bham.bioshock.communication.client;

import bham.bioshock.communication.common.Service;
import bham.bioshock.communication.interfaces.MessageHandler;
import bham.bioshock.communication.interfaces.MessageService;
import bham.bioshock.communication.messages.Message;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Interprets commands received from server */
public class ClientService extends Service implements MessageService, MessageHandler {

  private static final Logger logger = LogManager.getLogger(ClientService.class);

  private Socket socket;

  private MessageHandler handler;
  private boolean connectionCreated = false;

  /**
   * Creates the service and helper objects to send and receive messages
   *
   * @param _server socket
   * @param fromServer stream from server
   * @param toServer stream to server
   * @param client main client
   */
  public ClientService(Socket socket, ObjectInput fromServer, ObjectOutput toServer) {
    super(fromServer, toServer, "ClientService");
    connectionCreated = true;
    // save socket and streams for communication
    this.socket = socket;
  }

  public boolean isCreated() {
    return connectionCreated;
  }

  /**
   * Starts the sender and receiver threads
   */
  @Override
  public void run() {
    super.run();

    try {
      while (!isInterrupted()) {
        // Execute action from a blocking queue
        if (handler != null) {
          Message m = queue.poll(1000, TimeUnit.MILLISECONDS);
          if(m != null) {
            handler.handle(m);            
          }
        } else {
          sleep(200);
        }
      }
    } catch (InterruptedException e) {
      logger.trace("Client service was interrupted");
    } finally {
      connectionCreated = false;
      // wait for the threads to terminate and close the streams
      close();
      try {
        socket.close();
      } catch (IOException e) {}       
    }

  }
  
  /**
   * Register message handler
   * 
   * @param handler
   */
  public void registerHandler(MessageHandler handler) {
    this.handler = handler;
  }

  /**
   * Queue message to be sent
   */
  public void handle(Message action) {
    queue.add(action);
  }

  /**
   * Send the action to the server
   *
   * @param action to be sent
   */
  public void send(Message message) {
    if (!isCreated()) {
      logger.fatal("ClientService was not created! Message won't be sent!");
      return;
    }
    sender.send(message);
  }
  

}
