package bham.bioshock.minigame;

import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 * The Map Seeder.
 */
public class MapSeeder {

  /**
   * The world
   */
  private World world;

  /**
   * List of all the platforms in the world
   */
  private ArrayList<Platform> platforms = new ArrayList<>();

  /**
   * Instantiates a new Map seeder.
   *
   * @param w the w
   */
  public MapSeeder(World w) {
    this.world = w;
  }

  /**
   * Method to seed the world - generate a random number of both high and low platform frequency
   * areas which in themselves have varying numbers and sizes of platforms
   */
  public void seed() {

    // generate for top right quarter
    generateHighFreq(0, 2000, 0, 2000);

    // generate for bottom right quarter

    // generate for bottom left quarter

    // generate for top left quarter

    platforms.add(new Platform(world, new PlanetPosition(30, 2100), 100, 20));

    platforms.add(new Platform(world, -2050, 200, 200, 20));
    platforms.add(new Platform(world, -2050, 600, 200, 50));
    platforms.add(new Platform(world, -2100, 300, 10, 50));
    platforms.add(new Platform(world, -2250, 900, 500, 5));
    platforms.add(new Platform(world, -2450, 1100, 100, 50));
    platforms.add(new Platform(world, -2550, 1300, 200, 50));
    platforms.add(new Platform(world, -2550, 1400, 300, 25));
    platforms.add(new Platform(world, -2050, 1600, 300, 50));
    platforms.add(new Platform(world, -2050, 1400, 100, 100));
  }

  /**
   * Method to generate a high frequency platform area on the world
   *
   * @param minX the minimum X value of the area
   * @param maxX the maximum X value of the area
   * @param minY the minimum Y value of the area
   * @param maxY the maximum Y value of the area
   */
  public void generateHighFreq(float minX, float maxX, float minY, float maxY) {
    for (int i = 0; i < 20; i++){

    }
  }

  /**
   * Method to generate a medium frequency platform area on the world
   *
   * @param minX the minimum X value of the area
   * @param maxX the maximum X value of the area
   * @param minY the minimum Y value of the area
   * @param maxY the maximum Y value of the area
   */
  public void generateMedFreq(float minX, float maxX, float minY, float maxY) {

  }

  /**
   * Method to generate a low frequency platform area on the world
   *
   * @param minX the minimum X value of the area
   * @param maxX the maximum X value of the area
   * @param minY the minimum Y value of the area
   * @param maxY the maximum Y value of the area
   */
  public void generateLowFreq(float minX, float maxX, float minY, float maxY) {

  }

  /**
   * Gets a list of the platforms in the world.
   *
   * @return the list of platforms
   */
  public ArrayList<Platform> getPlatforms() {
    return platforms;
  }

}
