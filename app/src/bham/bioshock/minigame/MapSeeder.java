package bham.bioshock.minigame;

import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

public class MapSeeder {

  /** The world */
  private World world;

  /** Platforms */
  private ArrayList<Platform> platforms = new ArrayList<>();

  public MapSeeder(World w) {
    this.world = w;
  }

  public void seed() {
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

  public ArrayList<Platform> getPlatforms() {
    return platforms;
  }

}
