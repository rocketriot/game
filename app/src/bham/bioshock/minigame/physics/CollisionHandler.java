package bham.bioshock.minigame.physics;

import java.util.Optional;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.minigame.worlds.World.PlanetPosition;

public class CollisionHandler {
  
  private MinigameStore localStore;
  private World world;
  
  private final float MIN_DISTANCE = 500f * 500f;
  
  public CollisionHandler(MinigameStore localStore) {
    this.localStore = localStore;
    this.world = localStore.getWorld();
  }

  private MinimumTranslationVector applyCollision(Step step, Entity entity, Entity e) {
    if(e == entity) return null;
    if(step.position.sqDistanceFrom(e.getPos()) > MIN_DISTANCE) return null;
    if(!entity.canColideWith(e)) return null;
    
    MinimumTranslationVector v = checkCollision(step.position, entity, e);
    if(v != null) {
      entity.handleCollisionMove(step, v, e);
      step.addColide(e);
      return v;
    }
    return null;
  }
  
  public void applyCollisions(Step step, Entity entity) {
    for(Entity e : localStore.getStaticEntities()) {
      applyCollision(step, entity, e);
    }
  }
  
  public boolean applyDynamicCollisions(Step step, Entity entity) {
    boolean collided = false;
    for(Entity e : localStore.getEntities()) {
      MinimumTranslationVector vector = applyCollision(step, entity, e);
      collided = collided || (vector != null);
      if(vector != null) {
        step.position.x += vector.normal.x * vector.depth;
        step.position.y += vector.normal.y * vector.depth;        
      }
    }
    return collided;
  }
  
  public MinimumTranslationVector checkCollision(Polygon p1, Polygon p2) {
    if(p1 == null || p2 == null) return null; 
    MinimumTranslationVector v = new MinimumTranslationVector();
    if (Intersector.overlapConvexPolygons(p1, p2, v)) {
      return v;
    }
    return null;
  }
  
  public MinimumTranslationVector checkCollision(Position p, Entity entity, Entity collidable) {
    CollisionBoundary stepBoundary = entity.collisionBoundary().clone();
    stepBoundary.update(p, entity.getRotation(p.x, p.y));
    return checkCollision(stepBoundary, collidable.collisionBoundary());
  }
  
  private CollisionBoundary updatedCollisionBoundary(Position p, Entity e) {
    CollisionBoundary boundary = e.collisionBoundary().clone();
    boundary.update(p, e.getRotation(p.x, p.y));
    return boundary;
  }
  
  public MinimumTranslationVector checkCollision(Position p1, Entity entity, Position p2, Entity collidable) {
    CollisionBoundary stepBoundary1 = updatedCollisionBoundary(p1, entity);
    CollisionBoundary stepBoundary2 = updatedCollisionBoundary(p2, collidable);
    
    return checkCollision(stepBoundary1, stepBoundary2);
  }
  
  public void collide(Step step, float elastic, MinimumTranslationVector v) {
    Direction colPlace;
    Position pdelta = new Position(
        step.position.x + v.normal.x*v.depth, 
        step.position.y + v.normal.y*v.depth
    );
    PlanetPosition ppdelta = world.convert(pdelta);
    PlanetPosition pp = world.convert(step.position);
    double angleRatio = world.angleRatio(pp.fromCenter);
    
    if( Math.abs(ppdelta.angle - pp.angle) > 
      Math.abs(ppdelta.fromCenter - pp.fromCenter)*angleRatio ) { 
     
      if(ppdelta.angle < pp.angle ) {
        colPlace = Direction.RIGHT;
      } else {
        colPlace = Direction.LEFT;
      }
      
    } else {
      
      if(ppdelta.fromCenter < pp.fromCenter) {
        colPlace = Direction.UP;
      } else {
        colPlace = Direction.DOWN;
      }
    }
  
    
    double angleNorm = world.getAngleTo(step.position.x, step.position.y);
    double speedVBefore = step.vector.getValue();

    switch (colPlace) {
      case RIGHT:
        step.vector.stop(angleNorm + 90);
        step.vector.apply(angleNorm - 90, (speedVBefore - step.vector.getValue()) * elastic);
        break;
      case LEFT:
        step.vector.stop(angleNorm - 90);
        step.vector.apply(angleNorm + 90, (speedVBefore - step.vector.getValue()) * elastic);
        break;
      case DOWN:
        step.vector.stop(angleNorm + 180);
        step.vector.apply(angleNorm, (speedVBefore - step.vector.getValue()) * elastic);
      case UP:
        step.vector.stop(angleNorm);
        step.vector.apply(angleNorm + 180, (speedVBefore - step.vector.getValue()) * elastic);
      default:
        break;
    }
  }

}
