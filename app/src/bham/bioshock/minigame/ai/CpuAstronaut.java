package bham.bioshock.minigame.ai;

import java.util.ArrayList;
import java.util.Collection;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Astronaut.Move;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;

/**
 * The CPU Astronaut.
 */
public class CpuAstronaut {

  /**
   * The Astronaut.
   */
  Astronaut astronaut;

  /**
   * The current move.
   */
  Move move;

  /**
   * The World.
   */
  World world;

  /**
   * The Bullets.
   */
  ArrayList<Bullet> bullets = new ArrayList<>();

  /**
   * Instantiates a new CPU astronaut.
   *
   * @param a the a
   * @param w the w
   */
  public CpuAstronaut(Astronaut a, World w) {
    this.astronaut = a;
    this.world = w;
    move = new Move();
  }

  /**
   * Move left.
   */
  public void moveLeft() {
    move.movingLeft = true;
    move.movingRight = false;
  }

  /**
   * Move right.
   */
  public void moveRight() {
    move.movingLeft = false;
    move.movingRight = true;
  }

  /**
   * Jump.
   */
  public void jump() {
    move.jumping = true;
  }

  /**
   * Gets all the bullets.
   *
   * @return the bullets
   */
  public Collection<Bullet> getBullets() {
    return bullets;
  }

  /**
   * Clear bullets list.
   */
  public void clearBullets() {
    bullets.clear();
  }

  /**
   * Shoot.
   */
  public void shoot() {
    Position pos = astronaut.getPos();
    PlanetPosition pp = world.convert(pos);
    pp.fromCenter += astronaut.getHeight() / 2;

    SpeedVector speed = astronaut.getSpeedVector().copy();

    if (astronaut.getMove().movingRight) {
      pp.angle += world.angleRatio(pp.fromCenter) * 80;
      speed.apply(world.getAngleTo(pos.x, pos.y) + 90, Bullet.launchSpeed);
    } else {
      pp.angle -= world.angleRatio(pp.fromCenter) * 80;
      speed.apply(world.getAngleTo(pos.x, pos.y) - 90, Bullet.launchSpeed);
    }

    Position bulletPos = world.convert(pp);
    Bullet b = new Bullet(world, bulletPos.x, bulletPos.y, astronaut);

    // Apply bullet speed
    b.setSpeedVector(speed);
    bullets.add(b);
  }

  /**
   * Get astronaut.
   *
   * @return the astronaut
   */
  public Astronaut get() {
    return astronaut;
  }

  /**
   * End move.
   *
   * @return the move
   */
  public Move endMove() {
    Move oldMove = move;
    move = new Move();
    return oldMove;
  }

}
