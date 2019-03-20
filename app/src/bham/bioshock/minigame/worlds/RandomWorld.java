package bham.bioshock.minigame.worlds;

import bham.bioshock.minigame.seeders.PlatformSeeder;
import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.models.Rocket;
import java.util.Collections;

public class RandomWorld extends World {

  private static final long serialVersionUID = -5432716795106522826L;

  Position GRAVITY_POS = new Position(0f, 0f);
  double PLANET_RADIUS = 2000;
  double GRAVITY = 1500;
  Position[] playerPositions = new Position[4];
  ArrayList<Rocket> rockets = new ArrayList<>();
  ArrayList<Gun> guns = new ArrayList<>();
  ArrayList<Platform> platforms = new ArrayList<>();
  Position gravityCenter = new Position(0, 0);
  int textureNum;
  transient Texture texture;
  transient Texture frontTexture;

  public RandomWorld() {
    playerPositions[0] = new Position(-2300, 0);
    playerPositions[1] = new Position(0, -2000);
    playerPositions[2] = new Position(2000, 0);
    playerPositions[3] = new Position(0, 2000);

    guns.add(new Gun(this, -2070, -100));
    guns.add(new Gun(this, 2070, -100));
    guns.add(new Gun(this, 2070, 3000));
    guns.add(new Gun(this, -2070, -3100));
    guns.add(new Gun(this, 0, -3000));

    PlatformSeeder seeder = new PlatformSeeder(this);
    seeder.seed();
    platforms = seeder.getPlatforms();
    
    Random r = new Random();
    textureNum = (r.nextInt(100) % 4)+1;
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
    if (texture != null) {
      return texture;
    }
    texture = new Texture(Gdx.files.internal("app/assets/minigame/planets/" + textureNum + ".png"));
    if(textureNum == 4) {
      textureOffset = 770;
      frontTexture = new Texture(Gdx.files.internal("app/assets/minigame/planets/4_front.png"));
    }
    return texture;
  }


  /**
   * Method to get the platform path to a platform inclusive
   *
   * @param platform the goal platform
   * @return the path
   */
  public ArrayList<Platform> getPlatformPath(Platform platform) {
    ArrayList<Platform> path = new ArrayList<>();

    while (!((platform == null))) {
      path.add(platform);
      platform = platform.getParent();
    }
    Collections.reverse(path);
    return path;
  }


  @Override
  public void afterDraw(SpriteBatch batch) {
    if(textureNum == 4) {      
      batch.begin();
      float radius = (float) getPlanetRadius()+textureOffset;
      batch.draw(frontTexture, -radius, -radius, radius*2, radius*2);
      batch.end();      
    }
  }

}
