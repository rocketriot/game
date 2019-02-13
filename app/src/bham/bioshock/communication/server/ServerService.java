package bham.bioshock.communication.server;

import bham.bioshock.communication.Action;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

/** Executes the actions received by ServerReceiver. */
public class ServerService extends Thread {

  private ServerSender sender;
  private ServerReceiver receiver;
  private PriorityBlockingQueue<Action> queue = new PriorityBlockingQueue<>();
  private ServerHandler handler;
  private UUID id;

  public ServerService(
      ObjectInputStream fromClient, ObjectOutputStream toClient, ServerHandler handler) {
    // Sender and receiver for sending and receiving messages to/from user
    this.sender = new ServerSender(toClient);

    // Receiver for getting messages from user
    this.receiver = new ServerReceiver(this, fromClient);

    this.handler = handler;
  }

  public void run() {
    // start the receiver thread
    receiver.start();

    try {
      while (true) {
        // Execute actions from queue
        execute(queue.take());
      }
      // wait for the receiver to end
    } catch (InterruptedException e) {
      receiver.interrupt(); // end if receiver ends
      // This shouldn't actually happen
      System.err.println("ServerService was interrupted");
    }

    System.out.println("ServerService ending");
  }

  public void saveId(UUID id) {
    this.id = id;
  }
  
  public UUID Id() {
    return id;
  }
  
  public void store(Action action) {
    queue.add(action);
  }

  public void send(Action action) {
    sender.send(action);
  }

  /**
   * Delegates the execution to the appropriate method
   *
   * @param action to be executed
   */
  private void execute(Action action) {

    handler.handleRequest(action, this);
  }
}
