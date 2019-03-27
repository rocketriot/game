package bham.bioshock.minigame.seeders;

import static org.junit.jupiter.api.Assertions.assertTrue;

import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.testutils.minigame.FakeWorld;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/** The Platform seeding tests. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlatformSeedingTests {

  /** The World. */
  World world;

  /** The Platforms. */
  ArrayList<Platform> platforms = new ArrayList<>();

  /** Sets up the world and gets the platforms from it. */
  @BeforeAll
  public void setupTests() {
    world = new FakeWorld();
    ((FakeWorld) world).seedPlatforms();
    platforms = world.getPlatforms();
  }

  /** Platforms generated. */
  @Test
  public void platformsGenerated() {
    assertTrue(platforms.size() > 0);
  }
}
