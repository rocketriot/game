package bham.bioshock.minigame.models;

import org.junit.jupiter.api.*;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.worlds.*;
import static org.junit.jupiter.api.Assertions.*;

public class EntityTests {
  
  private Player createPlayer() {
    Player p = new Player(new FirstWorld());
    p.load();
    return p;
  }
  
  
  @Test
  public void testSimpleCollisions() {
    Player p1 = createPlayer();
    Player p2 = createPlayer();
    
    assertNotNull(p1.collisionBoundary());
    
    p1.setPosition(new Position(0, 0));
    p2.setPosition(new Position(76, 0));
    assertFalse(p1.checkCollision(p2));
    
    p2.setPosition(new Position(70, 0));
    assertTrue(p1.checkCollision(p2));
  }
  
  @Test
  public void testCollisionDirection() {
    
  }
  
}
