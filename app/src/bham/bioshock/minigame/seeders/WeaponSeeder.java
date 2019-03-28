package bham.bioshock.minigame.seeders;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.worlds.World;

import java.util.ArrayList;
import java.util.Random;

/** The Weapon seeder. */
public class WeaponSeeder {

  /** The world */
  private World world;

  /** List of all the guns in the world */
  private ArrayList<Gun> guns = new ArrayList<>();

  /**
   * Instantiates a new Weapon seeder.
   *
   * @param w the world
   */
  public WeaponSeeder(World w) {
    this.world = w;
  }

  /**
   * Method to seed the world with weapons.
   *
   * @return the array list of guns generated
   */
  public void seed() {
    guns.clear();
    // spawn gun in top left quarter
    generateWeapon(0, 90);
    // spawn gun in bottom left quarter
    generateWeapon(90, 180);
    // spawn gun in top right quarter
    generateWeapon(180, 270);
    // spawn gun in bottom right quarter
    generateWeapon(270, 360);
  }

  /**
   * Method to generate a weapon between two points in the world
   *
   * @param minAngle the minimum angle that the weapon can be generated between
   * @param maxAngle the maximum angle that the weapon can be generated between
   */
  private void generateWeapon(int minAngle, int maxAngle) {
    Random generator = new Random();

    float angle = (float) generator.nextInt(maxAngle - minAngle) + minAngle;
    float distance = 2250;
    PlanetPosition weaponPPos = new PlanetPosition(angle, distance);
    Position weaponPos = world.convert(weaponPPos);
    guns.add(new Gun(world, weaponPos.x, weaponPos.y));
  }

  /**
   * Get guns array list.
   *
   * @return the array list containing the guns
   */
  public ArrayList<Gun> getGuns() {
    return guns;
  }
}
