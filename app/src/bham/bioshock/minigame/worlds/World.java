package bham.bioshock.minigame.worlds;

import java.util.ArrayList;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.MapSeeder;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.Vector;

abstract public class World {


  public double getAngleTo(double x, double y) {
    double worldX = gravityCenter().x;
    double worldY = gravityCenter().y;
    return Vector.angleBetween(worldX, worldY, x, y);
  }

  public double getDistanceTo(double x, double y) {
    double dx = x - gravityCenter().x;
    double dy = y - gravityCenter().y;

    return Math.sqrt(dx * dx + dy * dy);
  }

  public double fromGroundTo(double x, double y) {
    return getDistanceTo(x, y) - getPlanetRadius();
  }

  public PlanetPosition convert(Position p) {
    return new PlanetPosition((float) getAngleTo(p.x, p.y), (float) getDistanceTo(p.x, p.y));
  }
  
  public double angleRatio(double r) {
    return 180 / (Math.PI * r);
  }

  public Position convert(PlanetPosition p) {
    double radians = Math.toRadians(p.angle);
    double dx = Math.sin(radians) * p.fromCenter;
    double dy = Math.cos(radians) * p.fromCenter;
    float x = (float) dx + gravityCenter().x;
    float y = (float) dy + gravityCenter().y;
    return new Position(x, y);
  }

  abstract public double getPlanetRadius();

  abstract public double getGravity();

  abstract public Position[] getPlayerPositions();

  abstract public Position gravityCenter();

  abstract public ArrayList<Rocket> getRockets();

  abstract public ArrayList<Gun> getGuns();

  abstract public ArrayList<Platform> getPlatforms();

  public class PlanetPosition {
    public float angle;
    public float fromCenter;

    public PlanetPosition(float angle, float fromCenter) {
      this.angle = angle;
      this.fromCenter = fromCenter;
    }
  }

}
