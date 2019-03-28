package bham.bioshock.minigame.physics;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.astronaut.AstronautMove;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.worlds.World;

public class StepsGenerator {
  
  private static final Logger logger = LogManager.getLogger(StepsGenerator.class);

  protected final double GROUND_FRICTION = 0.2;
  protected final double AIR_FRICTION = 0.15;
  private double jumpForce;
  private final int MAX_STEPS = 70;
  private float MOVE_SPEED = 600f;
  
  protected LinkedBlockingQueue<Step> steps = new LinkedBlockingQueue<>();
  private CollisionHandler collisionHandler = null;
  private Step lastStep;
  private Entity entity;
  private World world;
  private final float UNIT = 0.01f;
  private Generator generator;
  
  private AstronautMove currentMove = new AstronautMove();

  public StepsGenerator(World world, Entity entity) {
    this.jumpForce = world.getGravity() * 0.73;
    this.entity = entity;
    this.world = world;
    this.generator = new Generator();
  }

  public void generate() {
    if(!generator.isAlive()) {
      generator.start();      
    }
  }
  
  public void moveLeft() {
    if(!currentMove.movingLeft) {
      currentMove.movingLeft = true;
      currentMove.movingRight = false;
      this.reset();
    }
  }

  public void moveRight() {
    if(!currentMove.movingRight) {
      currentMove.movingRight = true;
      currentMove.movingLeft = false;
      this.reset();
    }
  }

  public void jump(boolean isJumping) {
    if(currentMove.jumping != isJumping) {
      currentMove.jumping = isJumping; 
      this.reset();
    }
  }

  public void moveStop() {
    if(currentMove.movingLeft || currentMove.movingRight) {
      currentMove.movingLeft = false;
      currentMove.movingRight = false;
      this.reset();
    }
  }
  
  
  public void updateFromServer(SpeedVector speed, Position pos) {
    synchronized(steps) {
      steps.clear();
      entity.setStep(new Step(pos, speed));
      lastStep = null;
    }
  }

  protected void reset() {
    synchronized(steps) {
      steps.clear();
      lastStep = null;
    }
  }
  
  public void stop() {
    generator.interrupt();
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
    
    int size = steps.size();
    // Remove first n steps
    for (int i = 0; i < Math.min(size, num)-1; i++) {
      steps.poll();
    }
    
    Step s = null;
    try {
      s = steps.poll(20, TimeUnit.MILLISECONDS); 
    } catch (InterruptedException e) {
    }
    
    fixUnderground(s);
        
    if(s != null) {
      checkDynamicCollisions(s); 
      return s;      
    }

    return null;
  }
  
  public void checkCollisions(Step step) {
    if(collisionHandler == null) return;
    collisionHandler.applyCollisions(step, entity);
  }
  
  private void fixUnderground(Step step) {
    if(step == null) return;
    double dist = entity.distanceFromGround(step.position.x, step.position.y);
    if(dist < -10) {
      step.updatePos(step.position.move(world).up((float) -(dist+10)).pos());
    }
  }
  
  public void checkDynamicCollisions(Step step) {
    if(collisionHandler == null) return;
    boolean collided = collisionHandler.applyDynamicCollisions(step, entity);
    if(collided) {
      synchronized(steps) {
        steps.clear();
        lastStep = step;        
      }
      // Make sure that doesn't go underground
      Position p = step.position;
      double angle = world.getAngleTo(p.x, p.y) + 180;
      if(!entity.isFlying(p.x, p.y)) {
        step.vector.stop(angle);
      }
      if(entity.distanceFromGround(p.x, p.y) < 0) {
        PlanetPosition pp = world.convert(p);
        pp.fromCenter -= entity.distanceFromGround(p.x, p.y);
        Position newp = world.convert(pp);
        step.position.x = newp.x;
        step.position.y = newp.y;
      }
    }
  }

  private class Generator extends Thread {

    public Generator() {
      super("Generator - " + entity.getClass().getSimpleName());
    }
    
    private final int DELAY = 0;

    private void applyMovement(double angle, SpeedVector speed) {
      if(currentMove.movingLeft) {
        speed.apply(angle + 90, MOVE_SPEED * GROUND_FRICTION);        
      } else if(currentMove.movingRight) {
        speed.apply(angle + 270, MOVE_SPEED * GROUND_FRICTION);        
      }
      if(currentMove.jumping) {
        double currentUp = speed.getValueFor(angle + 180);
        speed.apply(angle + 180, Math.max(0, (jumpForce - currentUp)));        
      }
    }
    
    private void applyFlyMovement(double angle, SpeedVector speed) {
      if(currentMove.movingLeft) {
        double currentLeft = speed.getValueFor(angle + 90);
        speed.apply(angle + 90, Math.max(0, MOVE_SPEED * AIR_FRICTION - currentLeft));        
      } else if(currentMove.movingRight) {
        double currentRight = speed.getValueFor(angle + 270);
        speed.apply(angle + 270, Math.max(0, MOVE_SPEED * AIR_FRICTION - currentRight));        
      }
    }
    
    public void run() {
      try {
        while (!isInterrupted()) {
          if (steps.size() >= MAX_STEPS) {
            sleep(DELAY + 20);
            continue;
          }

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
            
            if(!s.vector.dY().isNaN() && !s.vector.dX().isNaN()) {
              p.y += s.vector.dY() * UNIT;
              p.x += s.vector.dX() * UNIT;
            }
            steps.add(s);
            lastStep = s;
          }

          sleep(DELAY);
        }
      } catch (InterruptedException e) {
        logger.trace("Steps generator interrupted");
      }
    }
  }

}
