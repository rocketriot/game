package bham.bioshock.minigame.models;

import org.junit.jupiter.api.*;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.worlds.*;
import static org.junit.jupiter.api.Assertions.*;

public class EntityTests {
  
  private Astronaut createPlayer() {
    Astronaut p = new Astronaut(new FirstWorld(), 0, 0);
    p.load();
    return p;
  }
  
  
  @Test
  public void testSimpleCollisions() {
    Astronaut p1 = createPlayer();
    Astronaut p2 = createPlayer();
    
    assertNotNull(p1.collisionBoundary());
    
    p1.setPosition(new Position(0, 0));
    p2.setPosition(new Position(76, 0));
    assertNull(p1.checkCollision(p2));
    
    p2.setPosition(new Position(70, 0));
    assertNotNull(p1.checkCollision(p2));
  }
  
  @Test
  public void testCollisionDirection() {
    
  }
  
}
