package bham.bioshock.minigame.worlds;


import bham.bioshock.minigame.seeders.PlatformSeeder;
import bham.bioshock.minigame.seeders.WeaponSeeder;
import java.util.ArrayList;
import java.util.Random;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.models.Rocket;
import java.util.Collections;

public class RandomWorld extends World {

  private static final long serialVersionUID = -5432716795106522826L;

  Position GRAVITY_POS = new Position(0f, 0f);
  Position[] playerPositions = new Position[4];
  ArrayList<Rocket> rockets = new ArrayList<>();
  transient WeaponSeeder wSeeder;
  ArrayList<Gun> guns;
  transient PlatformSeeder pSeeder;
  ArrayList<Platform> platforms;
  Position gravityCenter = new Position(0, 0);
  
  public RandomWorld() {
    Random r = new Random();
    textureId = (r.nextInt(100) % 4)+1;
  }

  @Override
  public void init() {
    int radius = (int) getPlanetRadius();
    playerPositions[0] = new Position(-300-radius, 0);
    playerPositions[1] = new Position(0, -radius-300);
    playerPositions[2] = new Position(radius+300, 0);
    playerPositions[3] = new Position(0, radius+300);

    pSeeder = new PlatformSeeder(this);
    wSeeder = new WeaponSeeder(this);
    spawnPlatforms();
    spawnGuns();

    platforms = pSeeder.getPlatforms();
    guns = wSeeder.getGuns();
  }
  
  @Override
  public double getPlanetRadius() {
    return planetRadius;
  }

  @Override
  public double getGravity() {
    return gravity;
  }

  @Override
  public Position[] getPlayerPositions() {
    return playerPositions;
  }

  @Override
  public Position gravityCenter() {
    return gravityCenter;
  }

  @Override
  public ArrayList<Rocket> getRockets() {
    return rockets;
  }

  @Override
  public void spawnGuns(){
    wSeeder.seed();
  }

  @Override
  public ArrayList<Gun> getGuns() {
    return guns;
  }

  @Override
  public void spawnPlatforms() {
    pSeeder.seed();
  }

  public ArrayList<Platform> getPlatforms() {
    return platforms;
  }


  @Override
  public ArrayList<Platform> getPlatformPath(Platform platform) {
    ArrayList<Platform> path = new ArrayList<>();

    while (platform != null) {
      path.add(platform);
      platform = platform.getParent();
    }
    Collections.reverse(path);
    return path;
  }

}
