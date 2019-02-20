package bham.bioshock.minigame.worlds;

import java.util.ArrayList;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.Map;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Rocket;

public class FirstWorld extends World {
  
  Position GRAVITY_POS = new Position(0f, 0f);
  double PLANET_RADIUS = 2000;
  double GRAVITY = 1500;
  Position[] playerPositions = new Position[4];
  ArrayList<Rocket> rockets = new ArrayList<>();
  ArrayList<Gun> guns = new ArrayList<>();
  Position gravityCenter = new Position(0, 0);
  private Map map = new Map(this);
  
  public FirstWorld() {
    playerPositions[0] = new Position(-2000, 0);
    playerPositions[1] = new Position(0, -2000);
    playerPositions[2] = new Position(2000, 0);
    playerPositions[3] = new Position(0, 2000);
    
    rockets.add(new Rocket(this, 0, 2000, 1));
    rockets.add(new Rocket(this, 500, 2500, 2));

    guns.add(new Gun(this, -2070, -100));
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

  @Override
  public Map getMap(){return map;}
  
}
