package bham.bioshock.minigame.worlds;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.models.Rocket;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class JoinScreenWorld extends World {
  private static final long serialVersionUID = 1L;
  Position gravityCenter = new Position(0, 0);
  private double radius;
  private double gravity;

  public JoinScreenWorld() {
    radius = 5000;
    gravity = 2000;
  }

  @Override
  public double getPlanetRadius() {
    return radius;
  }

  @Override
  public double getGravity() {
    return gravity;
  }

  @Override
  public Position[] getPlayerPositions() {
    return new Position[0];
  }

  @Override
  public Position gravityCenter() {
    return gravityCenter;
  }

  @Override
  public ArrayList<Rocket> getRockets() {
    return null;
  }

  @Override
  public void spawnGuns() {}

  @Override
  public ArrayList<Gun> getGuns() {
    return null;
  }

  @Override
  public void spawnPlatforms() {}

  @Override
  public ArrayList<Platform> getPlatforms() {
    return null;
  }

  @Override
  public ArrayList<Platform> getPlatformPath(Platform platform) {
    return null;
  }

  @Override
  public void afterDraw(SpriteBatch batch) {}

  @Override
  public void init() {}
}
