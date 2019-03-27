package bham.bioshock.minigame.seeders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.testutils.minigame.FakeWorld;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/** The Weapon seeding tests. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WeaponSeedingTests {

  /** The World. */
  World world;

  /** The Guns. */
  ArrayList<Gun> guns = new ArrayList<>();

  /** Sets up the world and gets the guns from it. */
  @BeforeAll
  public void setupTests() {
    world = new FakeWorld();
    ((FakeWorld) world).seedWeapons();
    guns = world.getGuns();
  }

  /** Checks if the correct number of guns has been spawned. */
  @Test
  public void correctNumber() {
    assertEquals(4, guns.size());
  }

  /** Checks if the guns have been spawned in the correct palces in the world. */
  @Test
  public void correctQuarters() {
    boolean quarter1 = false;
    boolean quarter2 = false;
    boolean quarter3 = false;
    boolean quarter4 = false;

    for (Gun gun : guns) {
      float gunAng = gun.getPlanetPos().angle;
      if (gunAng >= 0 && gunAng < 90) {
        quarter1 = true;
      } else if (gunAng >= 90 && gunAng < 180) {
        quarter2 = true;
      } else if (gunAng >= 180 && gunAng < 270) {
        quarter3 = true;
      } else if (gunAng >= 270 && gunAng < 360) {
        quarter4 = true;
      }
    }
    assertTrue(quarter1 && quarter2 && quarter3 && quarter4);
  }
}
