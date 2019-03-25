package bham.bioshock.communication.server.handlers;

import static org.junit.Assert.*;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.utils.Clock;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.boardgame.GameBoardMessage;
import bham.bioshock.communication.messages.boardgame.StartGameMessage;
import bham.bioshock.communication.messages.joinscreen.AddPlayerMessage;
import bham.bioshock.communication.messages.joinscreen.JoinScreenMoveMessage;
import bham.bioshock.communication.messages.joinscreen.RegisterMessage;
import bham.bioshock.communication.messages.joinscreen.ServerFullMessage;
import bham.bioshock.server.InvalidMessageSequence;
import bham.bioshock.server.ServerHandler;
import bham.bioshock.testutils.communication.FakeMessage;
import bham.bioshock.testutils.communication.FakeServer;
import bham.bioshock.testutils.communication.FakeServerService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JoinScreenHandlerTest {
  
  Store store;
  FakeServer server;
  ServerHandler handler;
  Clock clock;

  @BeforeEach
  public void createFakeServer() {
    store = new Store();
    server = new FakeServer();
    clock = new Clock();
    handler = new ServerHandler(store, server, false, clock);
  }
  
  @Test
  public void testPlayerRegister() throws InvalidMessageSequence {
    FakeServerService service = new FakeServerService();
    handler.add(service);
    
    Player player = new Player("Tester");
    handler.handleRequest(new RegisterMessage(player), service);
    
    // Player is successfully registered
    assertTrue(service.Id().isPresent());
    assertEquals(player.getId(), service.Id().get());
    
    // Server should add the player to the host' store
    assertEquals(1, store.getPlayers().size());
    
    // Should get back the message with the connected player
    assertEquals(1, service.getSentMessages().size());
    AddPlayerMessage m = (AddPlayerMessage) service.getSentMessages().get(0);
    assertEquals(1, m.players.size());
    assertEquals(player.getId(), m.players.get(0).playerId);
    
    // Second "Register" message from the same client should be ignored
    Player player2 = new Player("Tester2");
    handler.handleRequest(new RegisterMessage(player2), service);
    // Service still have previous ID
    assertEquals(player.getId(), service.Id().get());
    // And no new message has been received
    assertEquals(1, service.getSentMessages().size());
  }
  
  @Test
  public void testHandleForUnregistered() {
    FakeServerService service = new FakeServerService();
    handler.add(service);
    try {
      handler.handleRequest(new FakeMessage(), service);
      fail("Invalid command sequence was not thrown");
    } catch(InvalidMessageSequence e) {
      // OK
    }
  }
  
  @Test
  public void testServerFull() throws InvalidMessageSequence {
    for(int i=0; i<4; i++) {
      FakeServerService service = new FakeServerService();
      handler.add(service);
      Player player = new Player("Tester" + i);
      handler.handleRequest(new RegisterMessage(player), service);
      assertEquals(1, service.getSentMessages().size());
      AddPlayerMessage m = (AddPlayerMessage) service.getSentMessages().get(0);
      assertEquals(i+1, m.players.size());
    }
    
    // Add 5th player - should be rejected
    FakeServerService serviceLast = new FakeServerService();
    handler.add(serviceLast);
    Player player = new Player("Tester Rejected");
    handler.handleRequest(new RegisterMessage(player), serviceLast);
    assertEquals(1, serviceLast.getSentMessages().size());
    assertTrue(serviceLast.getSentMessages().get(0) instanceof ServerFullMessage);
    assertEquals(4, store.getPlayers().size());
  }
  
  @Test
  public void testFillCPU() throws InvalidMessageSequence {
    // Add one player
    FakeServerService service = new FakeServerService();
    handler.add(service);
    Player player = new Player("Tester");
    handler.handleRequest(new RegisterMessage(player), service);
    service.clearMessages();
    
    handler.handleRequest(new StartGameMessage(), service);

    // Client should get only 1 message for starting the game
    assertEquals(1, service.getSentMessages().size());
    
    Message m = service.getSentMessages().get(0);
    assertTrue(m instanceof GameBoardMessage);
    GameBoardMessage gbm = (GameBoardMessage) m;
    assertEquals(4, gbm.coordinates.length);
    assertEquals(3, gbm.cpuPlayers.size());
    assertTrue(gbm.startGame);
  }
  
  @Test
  public void rocketMove() throws InvalidMessageSequence, InterruptedException {
    FakeServerService listener = new FakeServerService();
    handler.add(listener);
    handler.register(UUID.randomUUID(), "Listener", listener);
    
    FakeServerService service = new FakeServerService();
    handler.add(service);
    Player player = new Player("Tester");
    handler.handleRequest(new RegisterMessage(player), service);
    service.clearMessages();
    listener.clearMessages();
    
    // Create 2 messages, one after another
    JoinScreenMoveMessage oldMessage = new JoinScreenMoveMessage(player.getId(), new Position(10, 0), 5);
    Thread.sleep(1000);
    JoinScreenMoveMessage newMessage = new JoinScreenMoveMessage(player.getId(), new Position(0, 10), 0.4);
    
    // Send rocket position update
    handler.handleRequest(newMessage, service);
    
    // Client shouldn't get it's own message
    assertEquals(0, service.getSentMessages().size());
    
    assertEquals(1, listener.getSentMessages().size());
    assertTrue(listener.getSentMessages().get(0) instanceof JoinScreenMoveMessage);
    
    // Send old message which should be ignored
    handler.handleRequest(oldMessage, service);
    assertEquals(1, listener.getSentMessages().size());
  }
}
