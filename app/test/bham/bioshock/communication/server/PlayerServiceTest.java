package bham.bioshock.communication.server;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.*;
import bham.bioshock.testutils.communication.*;
import bham.bioshock.testutils.communication.streams.FakeObjectInput;
import bham.bioshock.testutils.communication.streams.FakeObjectOutput;
import bham.bioshock.testutils.server.FakeServerHandler;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerServiceTest {

  PlayerService service;
  FakeObjectInput input;
  FakeObjectOutput output;
  FakeServerHandler handler;
  
  @BeforeEach
  public void create() {
    input = new FakeObjectInput();
    output = new FakeObjectOutput();
    handler = new FakeServerHandler();
    
    service = new PlayerService(input, output, handler);
  }
  
  @Test
  public void testInterrupt() throws InterruptedException {
    service.start();
    
    Thread.sleep(200);
    
    // Should start all threads
    assertTrue(service.isRunning());
    
    service.abort();
    waitForAbort();
    
    // Should kill related threads
    assertTrue(service.aborted());
  }
  
  @Test
  public void testInputOutputClose() throws InterruptedException {
    service.start();
    Thread.sleep(200);
    
    // Streams should be open
    assertTrue(input.isOpen());
    assertTrue(output.isOpen());
    
    
    service.abort();
    waitForAbort();
    
    // Should close streams
    assertFalse(input.isOpen());
    assertFalse(input.isOpen());
  }
  
  public void waitForAbort() throws InterruptedException {
    service.join(1000);
    if(service.isAlive()) {
      fail("Service not finished");      
    }
  }
  
  @Test
  public void testServiceUnregister() throws InterruptedException {
    service.start();
    Thread.sleep(200);
    service.abort();
   
    waitForAbort();
    
    // Service should unregister in the handler after being aborted
    assertEquals(1, handler.unregistered.size());
  }
  
  @Test
  public void testId() {
    UUID id = UUID.randomUUID();
    service.saveId(id, "Test");
    assertTrue(service.Id().isPresent());
    assertEquals(id, service.Id().get());
  }
  
  @Test
  public void testMessageHandle() throws InterruptedException {
    service.start();
    assertEquals(0, handler.messages.size());
    
    input.add(new FakeMessage());
    Thread.sleep(100);
    assertEquals(1, handler.messages.size());
    
    service.abort();
    waitForAbort();
  }
  
  @Test
  public void testMessageSend() throws InterruptedException {
    service.start();
    assertEquals(0, output.messages.size());
    
    service.send(new FakeMessage());
    Thread.sleep(200);
    assertEquals(1, output.messages.size());
    assertTrue(output.messages.get(0) instanceof FakeMessage);
    
    service.abort();
    waitForAbort();
  }
  
  @Test
  public void testInputError() throws InterruptedException, IOException {
    service.start();
    Thread.sleep(200);
    
    input.close();
    
    // Closing the input stream should kill whole service
    waitForAbort();
  }

}
