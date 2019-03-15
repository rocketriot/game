package bham.bioshock.minigame.seeders;

import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.worlds.World;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.models.Rocket;
import java.util.Collections;

public class TestingWorld extends World {

  private static final long serialVersionUID = -5432716795106522826L;

  Position GRAVITY_POS = new Position(0f, 0f);
  double PLANET_RADIUS = 2000;
  double GRAVITY = 1500;
  Position[] playerPositions = new Position[4];
  ArrayList<Rocket> rockets = new ArrayList<>();
  ArrayList<Gun> guns;
  ArrayList<Platform> platforms;
  Position gravityCenter = new Position(0, 0);

  PlatformSeeder seeder;
  WeaponSeeder wSeeder;

  public TestingWorld() {

    seeder = new PlatformSeeder(this);
    wSeeder = new WeaponSeeder(this);
    seeder.seed();
    wSeeder.seed();
  }

  @Override
  public double getPlanetRadius() {
    return PLANET_RADIUS;
  }

  @Override
  public double getGravity() {
    return GRAVITY;
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
  public ArrayList<Gun> getGuns() {
    return guns;
  }


  public ArrayList<Platform> getPlatforms() {
    return platforms;
  }

  @Override
  public Texture getTexture() {
    return new Texture(Gdx.files.internal("app/assets/minigame/planet1.png"));
  }

  /**
   * Method to get the platform path to a platform
   * @param platform the paltform you want to get to
   * @return the platform path
   */
  public ArrayList<Platform> getPlatformPath(Platform platform){
    ArrayList<Platform> path = new ArrayList<>();
    Platform currentParent = new Platform(null, new PlanetPosition(0, 0), 0, 0);
    while (currentParent != null){
      currentParent = platform.getParent();
      path.add(currentParent);
    }
    Collections.reverse(path);
    return path;
  }

  public void seedWeapons(){
    wSeeder.seed();
    guns = wSeeder.getGuns();
  }

  public void seedPlatforms(){
    seeder.seed();
    platforms = seeder.getPlatforms();
  }
}
