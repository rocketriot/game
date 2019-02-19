package bham.bioshock.minigame.worlds;

import java.util.ArrayList;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Rocket;

abstract public class World {
    
  abstract public double getPlanetRadius();
  abstract public double getGravity();
  abstract public Position[] getPlayerPositions();
  abstract public Position gravityCenter();
  abstract public ArrayList<Rocket> getRockets();
  abstract public ArrayList<Gun> getGuns();
}
