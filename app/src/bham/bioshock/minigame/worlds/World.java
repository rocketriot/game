package bham.bioshock.minigame.worlds;

import bham.bioshock.common.Position;

abstract public class World {
    
  abstract public double getPlanetRadius();
  abstract public double getGravity();
  abstract public Position[] getPlayerPositions();
  abstract public Position gravityCenter();
}
