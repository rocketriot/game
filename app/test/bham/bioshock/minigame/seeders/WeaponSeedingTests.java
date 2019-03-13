package bham.bioshock.minigame.seeders;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bham.bioshock.minigame.worlds.World;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WeaponSeedingTests {

  World world;

  @BeforeAll
  public void setupTests() {
    world = new TestingWorld();
    ((TestingWorld) world).seedWeapons();
  }

  @Test
  public void correctNumber(){
    assertEquals(4, world.getGuns().size());
  }

}
