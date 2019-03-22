package bham.bioshock.minigame.seeders;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.World;
import java.util.ArrayList;
import java.util.Random;

/** The Map Seeder. */
public class PlatformSeeder {

  /** The world */
  private World world;

  /** List of all the platforms in the world */
  private ArrayList<Platform> platforms = new ArrayList<>();

  /**
   * Instantiates a new Map seeder.
   *
   * @param w the w
   */
  public PlatformSeeder(World w) {
    this.world = w;
  }

  /**
   * Method to seed the world - generate a random number of both high and low platform frequency
   * areas which in themselves have varying numbers and sizes of platforms
   */
  public void seed() {
    // generate for top right quarter
    generatePlatforms(0, 90, "High");
    // generate for bottom right quarter
    generatePlatforms(90, 180, "High");
    // generate for bottom left quarter
    generatePlatforms(180, 270, "High");
    // generate for top left quarter
    generatePlatforms(270, 360, "High");
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
        lowerBound = 1;
        upperBound = 5;
        break;
      case "Medium":
        lowerBound = 5;
        upperBound = 10;
        break;
      case "High":
        lowerBound = 10;
        upperBound = 15;
        break;
      default:
        break;
    }

    // get the total number of platforms according to the bounds set above
    Random generator = new Random();
    int totalPlatforms = generator.nextInt((upperBound - lowerBound) + 1) + lowerBound;
    int lvl1Count = totalPlatforms / 4;
    ArrayList<Platform> lvl1Platforms = new ArrayList<>();
    int lvl2Count = (totalPlatforms - lvl1Count) / 4;
    ArrayList<Platform> lvl2Platforms = new ArrayList<>();
    int lvl3Count = (totalPlatforms - lvl1Count - lvl2Count) / 2;
    ArrayList<Platform> lvl3Platforms = new ArrayList<>();
    int lvl4Count = totalPlatforms - lvl1Count - lvl2Count - lvl3Count;
    ArrayList<Platform> lvl4Platforms = new ArrayList<>();

    // generate the lvl1 platforms
    for (int i = 0; i < lvl1Count; i++) {

      ArrayList currentLevelAngles = new ArrayList();
      for (Platform p : lvl1Platforms) {
        currentLevelAngles.add(world.convert(p.getPos()).angle);
      }

      float angle = minAngle + generator.nextFloat() * (maxAngle - minAngle);

      while (tooClose(angle, currentLevelAngles)) {
        angle = minAngle + generator.nextFloat() * (maxAngle - minAngle);
      }

      int distance = 2150;
      int width = generator.nextInt((250 - 100) + 1) + 100;
      int height = 30;
      Platform platform = new Platform(world, new PlanetPosition(angle, distance), width, height);
      lvl1Platforms.add(platform);
      platforms.add(platform);
    }

    // generate the lvl2 platforms
    for (int i = 0; i < lvl2Count; i++) {
      if (lvl1Platforms.size() <= 0) {
        break;
      }
      Platform chosenPlatform = lvl1Platforms.get(generator.nextInt(lvl1Platforms.size()));
      float angle = generateAngle(chosenPlatform, lvl2Platforms, 12, 6);
      int distance = generator.nextInt((2450 - 2300) + 1) + 2300;
      int width = generator.nextInt((150 - 125) + 1) + 100;
      int height = 25;
      Platform platform = new Platform(world, new PlanetPosition(angle, distance), width, height);
      platform.setParent(chosenPlatform);
      lvl1Platforms.remove(chosenPlatform);
      lvl2Platforms.add(platform);
      platforms.add(platform);
    }

    // generate the lvl3 platforms
    for (int i = 0; i < lvl3Count; i++) {
      if (lvl2Platforms.size() <= 0) {
        break;
      }
      Platform chosenPlatform = lvl2Platforms.get(generator.nextInt(lvl2Platforms.size()));
      float angle = generateAngle(chosenPlatform, lvl3Platforms, 12, 6);
      int distance = generator.nextInt((2500 - 2450) + 1) + 2450;
      int width = generator.nextInt((125 - 100) + 1) + 100;
      int height = 20;
      Platform platform = new Platform(world, new PlanetPosition(angle, distance), width, height);
      platform.setParent(chosenPlatform);
      lvl2Platforms.remove(chosenPlatform);
      lvl3Platforms.add(platform);
      platforms.add(platform);
    }

    // generate the lvl4 platforms
    for (int i = 0; i < lvl4Count; i++) {
      if (lvl3Platforms.size() <= 0) {
        break;
      }
      Platform chosenPlatform =
          lvl3Platforms.get(Math.abs(generator.nextInt(lvl3Platforms.size())));
      float angle = generateAngle(chosenPlatform, lvl4Platforms, 12, 6);
      int distance = generator.nextInt((2750 - 2700) + 1) + 2700;
      int width = generator.nextInt((125 - 100) + 1) + 100;
      int height = 15;
      Platform platform = new Platform(world, new PlanetPosition(angle, distance), width, height);
      platform.setParent(chosenPlatform);
      lvl3Platforms.remove(chosenPlatform);
      lvl4Platforms.add(platform);
      platforms.add(platform);
    }
  }

  /**
   * Get a new angle dependent on angle from passed platform
   *
   * @param platform the passed platform
   * @param currentLevel all the platforms in the current level
   * @return the new angle
   */
  private float generateAngle(
      Platform platform, ArrayList<Platform> currentLevel, int max, int min) {

    ArrayList currentLevelAngles = new ArrayList();
    for (Platform p : currentLevel) {
      currentLevelAngles.add(world.convert(p.getPos()).angle);
    }

    Random generator = new Random();
    Position platformPos = platform.getPos();
    PlanetPosition platformPPos = world.convert(platformPos);
    float platformAngle = platformPPos.angle;
    float difference = (float) generator.nextInt((max - min) + 1) + min;
    float angle = platformAngle + difference;

    while (tooClose(angle, currentLevelAngles)) {
      difference = (float) generator.nextInt((max - min) + 1) + min;
      angle = platformAngle + difference;
    }
    return angle;
  }

  /**
   * Method to check whether a platform will be too close to an already existing platform on the
   * same level
   *
   * @param angle the angle to check
   * @param angles all the angles of platforms in the current level
   * @return whether the platform is too close
   */
  private boolean tooClose(float angle, ArrayList<Float> angles) {
    for (float a : angles) {
      if (Math.abs(a - angle) < 6) {
        return true;
      }
    }
    return false;
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
