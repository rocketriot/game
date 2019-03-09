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
  public void testCollisionDirection() {
    
  }
  
}
