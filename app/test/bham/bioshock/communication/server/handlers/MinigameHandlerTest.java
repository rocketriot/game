package bham.bioshock.communication.server.handlers;

import static org.junit.Assert.*;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.utils.Clock;
import bham.bioshock.communication.messages.minigame.EndMinigameMessage;
import bham.bioshock.communication.messages.minigame.MinigameStartMessage;
import bham.bioshock.communication.messages.minigame.RequestMinigameStartMessage;
import bham.bioshock.server.InvalidMessageSequence;
import bham.bioshock.server.ServerHandler;
import bham.bioshock.testutils.communication.FakeServer;
import bham.bioshock.testutils.communication.FakeServerService;

public class MinigameHandlerTest {
  
  Store store;
  FakeServer server;
  ServerHandler handler;
  FakeServerService listener;
  Clock clock;

  @BeforeEach
  public void createFakeServer() {
    store = new Store();
    server = new FakeServer();
    clock = new Clock();
    handler = new ServerHandler(store, server, false, clock);
    
    listener = new FakeServerService();
    handler.add(listener);
    handler.register(UUID.randomUUID(), "Listener", listener);
  }
  
  private FakeServerService registerService() {
    FakeServerService service = new FakeServerService();
    handler.add(service);
    handler.register(UUID.randomUUID(), "Test", service);
    return service;
  } 
  
  @Test
  public void minigameStartTest() throws InvalidMessageSequence, InterruptedException {
    UUID planetId = UUID.randomUUID();
    
    // Start with random objective
    RequestMinigameStartMessage startMessage = new RequestMinigameStartMessage(planetId);
    FakeServerService service = registerService();
    service.clearMessages();
    listener.clearMessages();
    
    handler.handleRequest(startMessage, service);
    
    assertEquals(1, service.getSentMessages().size());
    assertEquals(1, listener.getSentMessages().size());
    
    assertTrue(service.getSentMessages().get(0) instanceof MinigameStartMessage);
    
    MinigameStartMessage m = (MinigameStartMessage) service.getSentMessages().get(0);
    assertNotNull(m.objective);
    assertNotNull(m.world); 
    
    // Add 1 minute to the clock to stop the minigame
    clock.update(60 * 1000);
    Thread.sleep(2000);
    
    assertTrue(service.getSentMessages().get(1) instanceof EndMinigameMessage);
    
    EndMinigameMessage endMessage = (EndMinigameMessage) service.getSentMessages().get(1);
    assertEquals(planetId, endMessage.planetID);
    assertEquals(service.Id().get(), endMessage.playerID);
  }
  
}
