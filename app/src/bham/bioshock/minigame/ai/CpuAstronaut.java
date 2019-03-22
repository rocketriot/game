package bham.bioshock.minigame.ai;

import java.util.ArrayList;
import java.util.Collection;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;

public class CpuAstronaut {

  Astronaut astronaut;
  World world;
  ArrayList<Bullet> bullets = new ArrayList<>();

  public CpuAstronaut(Astronaut a, World w) {
    this.astronaut = a;
    this.world = w;
  }

  public void moveLeft() {
    astronaut.moveLeft(true);
    astronaut.moveRight(false);
  }

  public void moveRight() {
    astronaut.moveRight(true);
    astronaut.moveLeft(false);
  }

  public void jump() {
    astronaut.jump(true);
  }

  public void moveChange() {
    astronaut.moveChange();
    astronaut.jump(false);
  }

  public Collection<Bullet> getBullets() {
    return bullets;
  }

  public void clearBullets() {
    bullets.clear();
  }

  public void shoot() {
    Position pos = astronaut.getPos();
    PlanetPosition pp = world.convert(pos);
    pp.fromCenter += astronaut.getHeight() / 2;

    SpeedVector speed = (SpeedVector) astronaut.getSpeedVector().clone();

    if (astronaut.getMove().movingRight) {
      pp.angle += world.angleRatio(pp.fromCenter) * 80;
      speed.apply(world.getAngleTo(pos.x, pos.y)+90 , Bullet.launchSpeed);
    } else {
      pp.angle -= world.angleRatio(pp.fromCenter) * 80;
      speed.apply(world.getAngleTo(pos.x, pos.y)-90 , Bullet.launchSpeed);
    }

    Position bulletPos = world.convert(pp);
    Bullet b = new Bullet(world, bulletPos.x, bulletPos.y, astronaut);

    // Apply bullet speed
    b.setSpeedVector(speed);
    bullets.add(b);
  }

  public Astronaut get() {
    return astronaut;
  }
}
