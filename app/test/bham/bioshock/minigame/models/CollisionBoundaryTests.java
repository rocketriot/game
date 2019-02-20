package bham.bioshock.minigame.models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.physics.CollisionBoundary;

public class CollisionBoundaryTests {

  @Test
  public void collisionBoundaryDirection() {
    CollisionBoundary cb1 = new CollisionBoundary(10, 20);
    CollisionBoundary cb2 = new CollisionBoundary(10, 20);
    
    cb1.update(new Position(0, 100), 0);
    cb2.update(new Position(0, 0), 0);
    
//    assertEquals(Direction.DOWN, cb1.getDirectionTo(cb2));
//    assertEquals(Direction.UP, cb2.getDirectionTo(cb1));
//    
//    cb1.update(new Position(-100, 5), 0);
//    cb2.update(new Position(0, -5), 0);
//    
//    assertEquals(Direction.RIGHT, cb1.getDirectionTo(cb2));
//    assertEquals(Direction.LEFT, cb2.getDirectionTo(cb1));
//    
//    cb1.update(new Position(10, 20), 0);
//    cb2.update(new Position(0, -5), 0);
//    
//    assertEquals(Direction.NONE, cb1.getDirectionTo(cb2));
//    assertEquals(Direction.NONE, cb2.getDirectionTo(cb1));
  }
  
}
