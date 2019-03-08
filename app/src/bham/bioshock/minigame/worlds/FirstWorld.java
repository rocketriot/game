package bham.bioshock.minigame.worlds;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.MapSeeder;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.models.Rocket;

public class FirstWorld extends World {

  private static final long serialVersionUID = -5432716795106522826L;
  
  Position GRAVITY_POS = new Position(0f, 0f);
  double PLANET_RADIUS = 2000;
  double GRAVITY = 1500;
  Position[] playerPositions = new Position[4];
  ArrayList<Rocket> rockets = new ArrayList<>();
  ArrayList<Gun> guns = new ArrayList<>();
  ArrayList<Platform> platforms = new ArrayList<>();
  Position gravityCenter = new Position(0, 0);

  public FirstWorld() {
    playerPositions[0] = new Position(-2300, 0);
    playerPositions[1] = new Position(0, -2000);
    playerPositions[2] = new Position(2000, 0);
    playerPositions[3] = new Position(0, 2000);

    guns.add(new Gun(this, -2070, -100));
    guns.add(new Gun(this, 2070, -100));
    guns.add(new Gun(this, 2070, 3000));
    guns.add(new Gun(this, -2070, -3100));
    guns.add(new Gun(this, 0, -3000));
    
    MapSeeder seeder = new MapSeeder(this);
    seeder.seed();
    platforms = seeder.getPlatforms();
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
  
}
