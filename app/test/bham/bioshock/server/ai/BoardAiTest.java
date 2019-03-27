package bham.bioshock.server.ai;

import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.utils.Clock;
import bham.bioshock.communication.messages.boardgame.MovePlayerOnBoardMessage;
import bham.bioshock.communication.messages.boardgame.UpdateTurnMessage;
import bham.bioshock.server.handlers.GameBoardHandler;
import bham.bioshock.server.handlers.MinigameHandler;
import bham.bioshock.testutils.server.FakeServerHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardAiTest {

  Store store;
  FakeServerHandler handler;
  BoardAi boardAi;
  
  @BeforeEach
  public void create() {
    store = new Store();
    handler = new FakeServerHandler();
    GameBoardHandler gbHandler = new GameBoardHandler(store, handler, null);
    MinigameHandler mgHandler = new MinigameHandler(store, handler, new Clock());
    
    boardAi = new BoardAi(store, gbHandler, mgHandler);
  }
  
  @Test
  public void testInterrupted() throws InterruptedException {
    boardAi.start();
    
    Thread.sleep(1000);
    boardAi.interrupt();

    try {
      boardAi.join(2000);
    } catch(InterruptedException e) {
      fail("Should finish by now");      
    }   
  }
  
  @Test
  public void testInterrupted2() throws InterruptedException {
    boardAi.start();
    boardAi.interrupt();
    try {
      boardAi.join(1000);
    } catch(InterruptedException e) {
      fail("Should finish by now");      
    }   
  }
  
  @Test
  public void testInterruptedWithPlayer() throws InterruptedException {
    store.addPlayer(new Player("Tester"));
    boardAi.start();
    Thread.sleep(1000);
    boardAi.interrupt();

    try {
      boardAi.join(2000);
    } catch(InterruptedException e) {
      fail("Should finish by now");      
    }   
  }
  
  @Test
  public void testInterruptedWithCPUPlayer() throws InterruptedException {
    Player p = new Player("Tester", true);
    p.setCoordinates(new Coordinates(0, 0));
    store.addPlayer(p);
    GameBoard gb = new GameBoard();
    store.setGameBoard(gb);
    gb.generateGrid();
    
    boardAi.start();
    Thread.sleep(1000);
    boardAi.interrupt();

    try {
      boardAi.join(2000);
    } catch(InterruptedException e) {
      fail("Should finish by now");      
    }   
  }
  
  @Test
  public void waitForHumanPlayerTest() throws InterruptedException {
    Player human = new Player("Tester");
    Player cpu = new Player("Tester", true);
    
    GameBoard gb = new GameBoard();
    
    human.setCoordinates(new Coordinates(2, 0));    
    cpu.setCoordinates(new Coordinates(0, 0));
    
    store.addPlayer(human);
    store.addPlayer(cpu);
    
    store.setGameBoard(gb);
    gb.generateGrid();
    
    assertEquals(0, handler.sentMessages.size());
    assertEquals(human.getId(), store.getMovingPlayer().getId());
    boardAi.start();
    
    // Wait some time
    Thread.sleep(2000);
    
    // It should be the move of the same player
    assertEquals(human.getId(), store.getMovingPlayer().getId());
    assertEquals(0, store.getTurn());
    assertEquals(0, handler.sentMessages.size());

    boardAi.interrupt();
    boardAi.join();
  }
  
  @Test
  public void cpuMoveTest() throws InterruptedException {
    Player cpu = new Player("Tester1", true);
    Player human = new Player("Tester2");
    
    GameBoard gb = new GameBoard();
    store.setGameBoard(gb);
    gb.generateEmptyGrid();
    
    human.setCoordinates(new Coordinates(2, 0));    
    cpu.setCoordinates(new Coordinates(0, 0));
    
    store.addPlayer(cpu);
    store.addPlayer(human);
    
    assertEquals(0, handler.sentMessages.size());
    assertEquals(cpu.getId(), store.getMovingPlayer().getId());
    boardAi.start();
    
    // Wait some time
    Thread.sleep(2000);
    
    // Cpu should made the move
    assertEquals(2, handler.sentMessages.size());
    assertTrue(handler.sentMessages.get(0) instanceof MovePlayerOnBoardMessage);
    assertTrue(handler.sentMessages.get(1) instanceof UpdateTurnMessage);

    boardAi.interrupt();
    boardAi.join();
  }
  
  
  @Test
  public void cpuTestForce() throws InterruptedException {
    Player cpu = new Player("Tester1", true);
    Player human = new Player("Tester2");
    
    GameBoard gb = new GameBoard();
    store.setGameBoard(gb);
    gb.generateEmptyGrid();
    
    human.setCoordinates(new Coordinates(2, 0));    
    cpu.setCoordinates(new Coordinates(0, 0));
    
    store.addPlayer(cpu);
    store.addPlayer(human);
    
    assertEquals(0, handler.sentMessages.size());
    assertEquals(cpu.getId(), store.getMovingPlayer().getId());
    boardAi.start();
    

    Thread.sleep(2000);
    
    // Cpu should made the move
    assertEquals(2, handler.sentMessages.size());
    assertTrue(handler.sentMessages.get(0) instanceof MovePlayerOnBoardMessage);
    assertTrue(handler.sentMessages.get(1) instanceof UpdateTurnMessage);
    
    handler.sentMessages.clear();
    
    // Wait very long, AI should take action
    Thread.sleep(11000);
    
    assertEquals(1, handler.sentMessages.size());
    assertTrue(handler.sentMessages.get(0) instanceof UpdateTurnMessage);

    boardAi.interrupt();
    boardAi.join();
  }
}
