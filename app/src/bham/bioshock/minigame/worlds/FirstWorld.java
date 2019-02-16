package bham.bioshock.minigame.worlds;

import bham.bioshock.common.Position;

public class FirstWorld extends World {
  
  Position GRAVITY_POS = new Position(0f, 0f);
  double PLANET_RADIUS = 2000;
  double GRAVITY = 1500;
  Position[] playerPositions = new Position[4];
  Position gravityCenter = new Position(0, 0);
  
  
  public FirstWorld() {
    playerPositions[0] = new Position(-2000, 0);
    playerPositions[1] = new Position(0, -2000);
    playerPositions[2] = new Position(2000, 0);
    playerPositions[3] = new Position(0, 2000);
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
  
  

  
}
