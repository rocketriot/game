package bham.bioshock.common;

import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.testutils.minigame.FakeWorld;

public class PositionTest {

  @Test
  public void positionCopyTest() {
    Position p1 = new Position(2, 3);
    Position p2 = p1.copy();
    
    p1.x = 10;
    p1.y = 5;
    
    // p2 should be unchanged
    assertEquals(2, p2.x, 2);
    assertEquals(3, p2.y, 2);
  }
  
  @Test
  public void sqDistanceTest() {
    Position p1 = new Position(0, 0);
    Position p2 = new Position(10, 10);
    
    assertEquals(200f, p1.sqDistanceFrom(p2), 2);
    assertEquals(200f, p2.sqDistanceFrom(p1), 2);
    
    p1 = new Position(0, 10);
    p2 = new Position(0, 10);
    
    assertEquals(0f, p1.sqDistanceFrom(p2), 2);

    p1 = new Position(0, -10);
    p2 = new Position(0, 10);
    
    assertEquals(400f, p1.sqDistanceFrom(p2), 2);
  }
  
  @Test
  public void moveTest() {
    World w = new FakeWorld();
    Position p;
    p = (new Position(0, 10)).move(w).up(100).pos();
    assertEquals(110, p.y, 2);
    assertEquals(0, p.x, 2);
    
    p = (new Position(0, -10)).move(w).up(100).pos();
    assertEquals(-110, p.y, 2);
    assertEquals(0, p.x, 2);
    
    p = (new Position(10, 0)).move(w).up(100).pos();
    assertEquals(110, p.x, 2);
    assertEquals(0, p.y, 2);
    
    p = (new Position(-10, 0)).move(w).up(100).pos();
    assertEquals(-110, p.x, 2);
    assertEquals(0, p.y, 2);
    
    p = (new Position(10, 10)).move(w).up(100).pos();
    assertEquals(80.71, p.x, 2);
    assertEquals(80.71, p.y, 2);
    
    p = (new Position(-10, -10)).move(w).up(100).pos();
    assertEquals(-80.71, p.x, 2);
    assertEquals(-80.71, p.y, 2);
    
    p = (new Position(0, 50)).move(w).up(50).moveV(90).pos();
    assertEquals(100, p.x, 2);
    assertEquals(0, p.y, 2);
  }
}
