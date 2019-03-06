package bham.bioshock.minigame.physics;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.worlds.World;

public class StepsGenerator {
  
  private static final Logger logger = LogManager.getLogger(StepsGenerator.class);

  protected final double GROUND_FRICTION = 0.2;
  private final double JUMP_FORCE = 900;
  private final int MAX_STEPS = 100;
  private float MOVE_SPEED = 700f;
  private int MOVE_DELAY = 200;

  protected LinkedBlockingQueue<Step> steps = new LinkedBlockingQueue<>();
  private Step lastStep;
  private Entity entity;
  private World world;
  private final float UNIT = 0.02f;
  private Generator generator;
  
  private Move currentMove = Move.NONE;
  private int fromCurrentMove = 0;

  public StepsGenerator(World world, Entity entity) {
    this.entity = entity;
    this.world = world;
    this.generator = new Generator();
  }

  public void generate() {
    generator.start();
  }

  public void moveLeft() {
    saveMove(Move.LEFT);
  }

  public void moveRight() {
    saveMove(Move.RIGHT);
  }

  public void jump() {
    saveMove(Move.UP);
  }

  public void moveStop() {
    saveMove(Move.NONE);
  }
  
  private void saveMove(Move move) {
    if(currentMove != move) {
      steps.clear();
      fromCurrentMove = 0;
    }
    currentMove = move;
  }

  public Stream<Step> getFutureSteps() {
    return steps.stream();
  }

  public Step getStep(float delta) {
    int num = ((int) (delta / UNIT)) + 1;

    try {
      // Remove first n steps
      for (int i = 0; i < (num - 1); i++) {
        steps.take();
      }
      return steps.take();
    } catch (InterruptedException e) {
      logger.fatal("Interrupted while getting a step");
    }
    return null;
  }

  private class Generator extends Thread {

    private final int DELAY = 10;

    private void applyMovement(double angle, SpeedVector speed) {
      switch(currentMove) {
        case LEFT:
          speed.apply(angle + 90, MOVE_SPEED * GROUND_FRICTION);
          break;
        case RIGHT:
          speed.apply(angle + 270, MOVE_SPEED * GROUND_FRICTION);
          break;
        case UP:
          speed.apply(angle + 180, JUMP_FORCE);
          break;
      }
    }
    
    public void run() {
      try {
        while (!isInterrupted()) {
          if (steps.size() >= MAX_STEPS) {
            sleep(DELAY);
            continue;
          };

          Step last;
          if (!steps.isEmpty()) {
            last = lastStep;
          } else {
            last = entity.currentStep();
          }

          double angle = world.getAngleTo(last.position.x, last.position.y) + 180;
          Position p = new Position(last.position.x, last.position.y);
          SpeedVector speed = new SpeedVector(last.vector.dX(), last.vector.dY());
          
          if (entity.isFlying(p.x, p.y)) {
            speed.apply(angle, world.getGravity() * UNIT);
          } else {
            applyMovement(angle, speed);
            speed.friction(GROUND_FRICTION);
            speed.stop(angle);
          }
          
          p.y += speed.dY() * UNIT;
          p.x += speed.dX() * UNIT;
          
          Step s = new Step(p, speed);
          steps.add(s);
          lastStep = s;

          sleep(DELAY);
        }
      } catch (InterruptedException e) {
        logger.debug("Steps generator interrupted");
      }
    }
  }
  
  private enum Move {
    LEFT, RIGHT, UP, NONE
  }
}
