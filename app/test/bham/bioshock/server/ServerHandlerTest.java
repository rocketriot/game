package bham.bioshock.server;

import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.utils.Clock;
import bham.bioshock.communication.messages.joinscreen.DisconnectPlayerMessage;
import bham.bioshock.communication.messages.joinscreen.RegisterMessage;
import bham.bioshock.testutils.communication.FakeMessage;
import bham.bioshock.testutils.communication.FakeServerService;
import bham.bioshock.testutils.server.FakeServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerHandlerTest {

  Store store;
  FakeServer server;
  ServerHandler handler;
  Clock clock;

  @BeforeEach
  public void createFakeServer() {
    store = new Store();
    server = new FakeServer();
    clock = new Clock();
    handler = new ServerHandler(store, false, clock);
  }
  
  @Test
  public void testServerHandler() {
    FakeServerService service = new FakeServerService();
    handler.add(service);
    
    // Shouldn't send a message to not registered service
    handler.sendToAll(new FakeMessage());
    assertTrue(service.getSentMessages().isEmpty());
    
    // Register the service, ID should be saved in the service
    UUID serviceId = UUID.randomUUID();
    handler.register(serviceId, "TestUser", service);
    assertTrue(service.Id().isPresent());
    assertEquals(serviceId, service.Id().get());
    
    // Should receive a message
    handler.sendTo(serviceId, new FakeMessage());
    assertEquals(1, service.getSentMessages().size());
    
    // Should receive a message
    handler.sendToAll(new FakeMessage());
    assertEquals(2, service.getSentMessages().size());    
    
    // Should receive a message
    handler.sendToAllExcept(new FakeMessage(), UUID.randomUUID());
    assertEquals(3, service.getSentMessages().size());
    
    // Shouldn't receive a message
    handler.sendToAllExcept(new FakeMessage(), serviceId);
    assertEquals(3, service.getSentMessages().size());   
    
    // Shouldn't receive a message
    handler.sendTo(UUID.randomUUID(), new FakeMessage());
    assertEquals(3, service.getSentMessages().size()); 
  }
  
  @Test
  public void abortTest() {
    // Add one registered service
    FakeServerService service1 = new FakeServerService();
    handler.add(service1);
    UUID serviceId = UUID.randomUUID();
    service1.start();
    handler.register(serviceId, "TestUser", service1);
    
    // Add one unregistered service
    FakeServerService service2 = new FakeServerService();
    service2.start();
    handler.add(service2);
    
    assertTrue(service1.isRunning());
    assertTrue(service2.isRunning());
    
    // Abort
    handler.abort();
    
    // Both services should be stopped
    assertFalse(service1.isRunning());
    assertFalse(service2.isRunning());
  }
  
  @Test
  public void unregisterTest() {
    FakeServerService listenerService = new FakeServerService();
    handler.add(listenerService);
    handler.register(UUID.randomUUID(), "Listener", listenerService);
    listenerService.clearMessages();
    
    // Add unregistered service
    FakeServerService service = new FakeServerService();
    handler.add(service);
    handler.unregister(service);
    
    try {
      handler.handleRequest(new RegisterMessage(new Player("test")), service);
      fail("Message handled by unregistered service");
    } catch (InvalidMessageSequence e) {
      // OK
    }
    
    // After unregistering of unregistered service no message should be sent
    assertEquals(0, listenerService.getSentMessages().size());
    
    // Add one registered service
    FakeServerService service2 = new FakeServerService();
    handler.add(service2);
    handler.register(UUID.randomUUID(), "TestUser", service2);
    
    // service is registered
    assertTrue(service2.Id().isPresent());
    
    // Unregister
    handler.unregister(service2);
    
    try {
      handler.handleRequest(new FakeMessage(), service2);
      fail("Message handled by unregistered service");
    } catch (InvalidMessageSequence e) {
      // OK
    }
    
    // After unregistering of registered service disconnect message should be sent
    assertEquals(1, listenerService.getSentMessages().size());
    assertTrue(listenerService.getSentMessages().get(0) instanceof DisconnectPlayerMessage);
    
    
  }
}
