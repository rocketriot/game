package bham.bioshock.minigame.seeders;

import static org.junit.jupiter.api.Assertions.assertTrue;

import bham.bioshock.minigame.worlds.World;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlatformSeedingTests {

  World world;

  @BeforeAll
  public void setupTests() {
    world = new TestingWorld();
    ((TestingWorld) world).seedPlatforms();
  }

  @Test
  public void platformsGenerated() {
    assertTrue(world.getPlatforms().size() > 0);
  }

}
