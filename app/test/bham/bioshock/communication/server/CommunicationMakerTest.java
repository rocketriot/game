package bham.bioshock.communication.server;

import static org.junit.Assert.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketImplFactory;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import bham.bioshock.testutils.communication.streams.*;
import bham.bioshock.testutils.server.FakeServerHandler;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommunicationMakerTest {

  CommunicationMaker connector;
  FakeServerHandler handler;
  ServerSocket serverSocket;
  FakeSocketImpl socket;
  FakeObjectInput objectInput;
  FakeObjectOutput objectOutput;
  
  @BeforeEach
  public void create() throws IOException {
    if(socket != null) {
      socket.clear();      
    }
    handler = new FakeServerHandler();
    connector = new CommunicationMaker(new FakeStreamFactory(objectOutput, objectInput));

    SocketImplFactory factory = new SocketImplFactory() {
      public SocketImpl createSocketImpl() {
        if(socket != null) {
          return socket;
        }
        socket = new FakeSocketImpl();
        return socket;
      }
    };
    
    try {
      ServerSocket.setSocketFactory(factory);
      Socket.setSocketImplFactory(factory);      
    } catch(SocketException e) {
      
    }
    serverSocket = new ServerSocket(5000);
  }
  
  @AfterEach
  public void stopServices() {
    // Stop all other threads
    handler.abort();
  }
  
  @Test
  public void abortTest() throws InterruptedException {
    connector.startSearch(handler, serverSocket, UUID.randomUUID(), false);
    // Init new connection
    socket.queue.add(new Object());
    
    // Server socket should be still running
    assertFalse(serverSocket.isClosed());
    
    connector.disconnect();
    
    // Should kill the service
    connector.join(1000);
    if(connector.isAlive()) {
      fail("Service not finished");      
    }

    // Server socket should be closed
    assertTrue(serverSocket.isClosed());
  }
  
  @Test
  public void serviceWithoutSocketTest() throws InterruptedException {
    connector.start();
    Thread.sleep(200);
    
    // Communication maker shouldn't start without a socket
    assertFalse(connector.isAlive());
  }
  
  @Test
  public void fullStopTest() throws InterruptedException {
    connector.startSearch(handler, serverSocket, UUID.randomUUID(), true);
    
    Thread.sleep(1000);
    // That should stop both the main thread and the discovery thread
    connector.disconnect();
    Thread.sleep(1000);
    
    // Stopping discovery shouldn't stop the connector
    assertTrue(connector.aborted());
    
    connector.join(1000);
  }
  
}
