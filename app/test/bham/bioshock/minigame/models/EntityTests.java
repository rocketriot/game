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
  public void testCollisionDirection() {
    
  }
  
}
