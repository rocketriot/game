package bham.bioshock.minigame.ai;

import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.models.astronaut.AstronautMove;
import bham.bioshock.minigame.worlds.World;

import java.util.ArrayList;
import java.util.Collection;

/** The CPU Astronaut. */
public class CpuAstronaut {

  /** The Astronaut. */
  Astronaut astronaut;

  /** The current move. */
  AstronautMove move;

  /** The World. */
  World world;

  /** The Bullets. */
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
    move = new AstronautMove();
  }

  /** Move left. */
  public void moveLeft() {
    move.movingLeft = true;
    move.movingRight = false;
  }

  /** Move right. */
  public void moveRight() {
    move.movingLeft = false;
    move.movingRight = true;
  }

  /** Jump. */
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

  /** Clear bullets list. */
  public void clearBullets() {
    bullets.clear();
  }

  /** Shoot. */
  public void shoot() {
    Bullet b = Bullet.createForPlayer(world, astronaut);
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
  public AstronautMove endMove() {
    AstronautMove oldMove = move;
    move = new AstronautMove();
    return oldMove;
  }
}
