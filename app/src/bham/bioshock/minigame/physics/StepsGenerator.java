package bham.bioshock.minigame.physics;

import java.util.Optional;
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
  protected final double AIR_FRICTION = 0.15;
  private final double JUMP_FORCE = 1200;
  private final int MAX_STEPS = 100;
  private float MOVE_SPEED = 700f;
  
  protected LinkedBlockingQueue<Step> steps = new LinkedBlockingQueue<>();
  private CollisionHandler collisionHandler = null;
  private Step lastStep;
  private Entity entity;
  private World world;
  private final float UNIT = 0.02f;
  private Generator generator;
  
  private Move currentMove = Move.NONE;
  private boolean jump = false;

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

  public void jump(boolean isJumping) {
    if(jump != isJumping) {
      jump = isJumping; 
      synchronized(steps) {
        steps.clear();
        lastStep = null;
      }
    }
  }

  public void moveStop() {
    saveMove(Move.NONE);
  }
  
  private void saveMove(Move move) {
    if(currentMove != move) {
      synchronized(steps) {
        steps.clear();
        lastStep = null;
      }
    }
    currentMove = move;
  }

  public Stream<Step> getFutureSteps() {
    return steps.stream();
  }
  
  public Optional<Step> getFutureStep(int n) {
    if(steps.size() < n) {
      if (lastStep != null) {
        return Optional.of(lastStep);
      }
      return Optional.of(entity.currentStep());
    }
    return steps.stream().skip(n).findFirst();
  }
  
  public void setCollisionHandler(CollisionHandler collisionHandler) {
    this.collisionHandler = collisionHandler;
  }

  public Step getStep(float delta) {
    int num = ((int) (delta / UNIT)) + 1;
    
    try {
      int size = steps.size();
      // Remove first n steps
      for (int i = 0; i < Math.min(size, num)-1; i++) {
        steps.poll();
      }

      
      Step s = steps.take();
      checkDynamicCollisions(s); 
      return s;
      
    } catch (InterruptedException e) {
      logger.fatal("Interrupted while getting a step");
    }
    return null;
  }
  
  public void checkCollisions(Step step) {
    if(collisionHandler == null) return;
    collisionHandler.applyCollisions(step, entity);
  }
  
  public void checkDynamicCollisions(Step step) {
    if(collisionHandler == null) return;
    boolean collided = collisionHandler.applyDynamicCollisions(step, entity);
    if(collided) {
      synchronized(steps) {
        steps.clear();
        lastStep = step;        
      }
    }
  }

  private class Generator extends Thread {

    private final int DELAY = 0;

    private void applyMovement(double angle, SpeedVector speed) {
      switch(currentMove) {
        case LEFT:
          speed.apply(angle + 90, MOVE_SPEED * GROUND_FRICTION);
          break;
        case RIGHT:
          speed.apply(angle + 270, MOVE_SPEED * GROUND_FRICTION);
          break;
        default:
          break;
      }
      if(jump) {
        double currentUp = speed.getValueFor(angle + 180);
        speed.apply(angle + 180, Math.max(0, (JUMP_FORCE - currentUp)));        
      }
    }
    
    private void applyFlyMovement(double angle, SpeedVector speed) {
      switch(currentMove) {
        case LEFT:
          double currentLeft = speed.getValueFor(angle + 90);
          speed.apply(angle + 90, Math.max(0, MOVE_SPEED * AIR_FRICTION - currentLeft));
          break;
        case RIGHT:
          double currentRight = speed.getValueFor(angle + 270);
          speed.apply(angle + 270, Math.max(0, MOVE_SPEED * AIR_FRICTION - currentRight));
          break;
        default:
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

          synchronized (steps) {
            Step last;
            if (!steps.isEmpty() && lastStep != null) {
              last = lastStep;
            } else {
              last = entity.currentStep();
            }
  
            double angle = world.getAngleTo(last.position.x, last.position.y) + 180;
            Position p = new Position(last.position.x, last.position.y);
            SpeedVector speed = new SpeedVector(last.vector.dX(), last.vector.dY());
            
            Step s = new Step(p, speed);
            checkCollisions(s);
            
            if (entity.isFlying(p.x, p.y) && !s.getOnGround()) {
              speed.apply(angle, world.getGravity() * UNIT);
              applyFlyMovement(angle, speed);
            } else {
              applyMovement(angle, speed);
              speed.friction(GROUND_FRICTION);
              speed.stop(angle);
            }
            
            p.y += s.vector.dY() * UNIT;
            p.x += s.vector.dX() * UNIT;
  
            steps.add(s);
            lastStep = s;
          }

          sleep(DELAY);
        }
      } catch (InterruptedException e) {
        logger.debug("Steps generator interrupted");
      }
    }
  }
  
  private enum Move {
    LEFT, RIGHT, NONE
  }
}
