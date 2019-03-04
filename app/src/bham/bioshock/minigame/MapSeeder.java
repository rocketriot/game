package bham.bioshock.minigame;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Random;

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
    generatePlatforms(0, 90, generateFrequency());
    // generate for bottom right quarter
    generatePlatforms(90, 180, generateFrequency());
    // generate for bottom left quarter
    generatePlatforms(180, 270, generateFrequency());
    // generate for top left quarter
    generatePlatforms(270, 360, generateFrequency());
  }

  /**
   * Method to generate whether the part of the map will have a low, medium or high frequency of
   * platforms
   *
   * @return a string corresponding to the frequency
   */
  private String generateFrequency() {
    // a result of 1 is low frequency, 2 is medium and 3 is high
    Random generator = new Random();
    int frequency = generator.nextInt((3 - 1) + 1) + 1;

    switch (frequency) {
      case 1:
        return "Low";
      case 2:
        return "Medium";
      case 3:
        return "High";
      default:
        System.out.println("Somehow an int between 1 and 3 hasn't been generated");
        return null;
    }
  }

  /**
   * Method to generate platforms for an area of the world depending on the passed frequency
   *
   * @param min the min angle to generate platforms between
   * @param max the max angle to generate platforms between
   * @param frequency the frequency of platforms
   */
  public void generatePlatforms(float min, float max, String frequency) {
    int minAngle = (int) min;
    int maxAngle = (int) max;

    int lowerBound = 0;
    int upperBound = 0;

    // set the bounds of the number of platforms
    switch (frequency) {
      case "Low":
        lowerBound = 0;
        upperBound = 10;
      case "Medium":
        lowerBound = 10;
        upperBound = 15;
      case "High":
        lowerBound = 15;
        upperBound = 20;

      default:
        System.out.println("Invalid seed frequency");
        break;
    }

    // get the total number of platforms according to the bounds set above
    Random generator = new Random();
    int totalPlatforms = generator.nextInt((upperBound - lowerBound) + 1) + lowerBound;
    int lvl1Platforms = totalPlatforms / 2;
    ArrayList<Platform> lvl1 = new ArrayList<>();
    int lvl2Platforms = (totalPlatforms - lvl1Platforms) / 2;
    ArrayList<Platform> lvl2 = new ArrayList<>();
    int lvl3Platforms = (totalPlatforms - lvl1Platforms - lvl2Platforms) / 2;
    ArrayList<Platform> lvl3 = new ArrayList<>();
    int lvl4Platforms = totalPlatforms - lvl1Platforms - lvl2Platforms - lvl3Platforms;
    ArrayList<Platform> lvl4 = new ArrayList<>();

    // generate the lvl1 platforms
    for (int i = 0; i < lvl1Platforms; i++) {
      float angle = minAngle + generator.nextFloat() * (maxAngle - minAngle);
      int distance = 2150;
      int width = generator.nextInt((250 - 50) + 1) + 50;
      int height = generator.nextInt((30 - 10) + 1) + 10;
      Platform platform = new Platform(world, new PlanetPosition(angle, distance), width, height);
      lvl1.add(platform);
      platforms.add(platform);
    }

    // generate the lvl2 platforms
    for (int i = 0; i < lvl2Platforms; i++) {
      Platform chosenPlatform = lvl1.get(generator.nextInt(lvl1.size()));
      float angle = generateAngle(chosenPlatform, generator);
      int distance = generator.nextInt((2400 - 2300) + 1) + 2300;
      int width = generator.nextInt((200 - 50) + 1) + 50;
      int height = generator.nextInt((25 - 10) + 1) + 10;
      Platform platform = new Platform(world, new PlanetPosition(angle, distance), width, height);
      lvl2.add(platform);
      platforms.add(platform);
    }

    // generate the lvl3 platforms
    for (int i = 0; i < lvl3Platforms; i++) {
      Platform chosenPlatform = lvl2.get(generator.nextInt(lvl2.size()));
      float angle = generateAngle(chosenPlatform, generator);
      int distance = generator.nextInt((2600 - 2500) + 1) + 2500;
      int width = generator.nextInt((150 - 50) + 1) + 50;
      int height = generator.nextInt((25 - 10) + 1) + 10;
      Platform platform = new Platform(world, new PlanetPosition(angle, distance), width, height);
      lvl3.add(platform);
      platforms.add(platform);
    }

    // generate the lvl4 platforms
    for (int i = 0; i < lvl4Platforms; i++) {
      Platform chosenPlatform = lvl3.get(generator.nextInt(lvl3.size()));
      float angle = generateAngle(chosenPlatform, generator);
      int distance = generator.nextInt((2750 - 2700) + 1) + 2600;
      int width = generator.nextInt((100 - 50) + 1) + 50;
      int height = generator.nextInt((20 - 10) + 1) + 10;
      Platform platform = new Platform(world, new PlanetPosition(angle, distance), width, height);
      lvl4.add(platform);
      platforms.add(platform);
    }
  }

  /**
   * Get a new angle dependent on angle from passed platform
   *
   * @param platform the passed platform
   * @param generator the instance of Random instantiated in Seed()
   * @return the new angle
   */
  private float generateAngle(Platform platform, Random generator) {
    Position platformPos = platform.getPos();
    PlanetPosition platformPPos = world.convert(platformPos);
    float platformAngle = platformPPos.angle;
    int difference = generator.nextInt((20 - 10) + 1) + 10;
    return (platformAngle + difference);
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
