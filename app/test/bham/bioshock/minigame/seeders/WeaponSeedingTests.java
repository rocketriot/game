package bham.bioshock.minigame.seeders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.worlds.World;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WeaponSeedingTests {

  World world;
  ArrayList<Gun> guns = new ArrayList<>();

  @BeforeAll
  public void setupTests() {
    world = new TestingWorld();
    ((TestingWorld) world).seedWeapons();
    guns = world.getGuns();
  }

  @Test
  public void correctNumber() {
    assertEquals(4, guns.size());
  }

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
