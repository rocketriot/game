package bham.bioshock.minigame.physics;

import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.Polygon;

public class CollisionHandler {

  /** Minimal distance between objects to check the collision (squared) */
  private final float MIN_DISTANCE = 500f * 500f;

  private MinigameStore localStore;
  private World world;

  public CollisionHandler(MinigameStore localStore) {
    this.localStore = localStore;
    this.world = localStore.getWorld();
  }

  /**
   * Check the collision between entities in specific step and update step vector and position to
   * reflect the collision
   *
   * @param step in which collision is checked
   * @param entity
   * @param e
   * @return vector to resolve the collision if one happened
   */
  private MinimumTranslationVector applyCollision(Step step, Entity entity, Entity e) {
    if (e == entity) return null;
    if (step.position.sqDistanceFrom(e.getPos()) > MIN_DISTANCE) return null;
    if (!entity.canCollideWith(e)) return null;

    MinimumTranslationVector v = checkCollision(step.position, entity, e);
    if (v != null) {
      boolean shouldResolve = entity.handleCollisionMove(step, v, e);
      step.addColide(e);
      if (shouldResolve) {
        return v;
      }
      return null;
    }
    return null;
  }

  /**
   * Check & apply collisions to the step for all static entities in the store
   *
   * @param step
   * @param entity
   */
  public void applyCollisions(Step step, Entity entity) {
    for (Entity e : localStore.getStaticEntities()) {
      applyCollision(step, entity, e);
    }
  }

  /**
   * Check & apply collisions to the step for dynamic entities in the store This is checked just
   * before the step is taken from the generator
   *
   * @param step
   * @param entity
   * @return true if any collision happened
   */
  public boolean applyDynamicCollisions(Step step, Entity entity) {
    boolean collided = false;
    for (Entity e : localStore.getEntities()) {
      MinimumTranslationVector vector = applyCollision(step, entity, e);
      collided = collided || (vector != null);
      // Update current position to resolve collision
      if (vector != null) {
        step.position.x += vector.normal.x * vector.depth;
        step.position.y += vector.normal.y * vector.depth;
      }
    }
    return collided;
  }

  /**
   * Check the collision between two polygons
   *
   * @param p1
   * @param p2
   * @return vector to resolve the collision if one happened
   */
  public MinimumTranslationVector checkCollision(Polygon p1, Polygon p2) {
    if (p1 == null || p2 == null) return null;
    MinimumTranslationVector v = new MinimumTranslationVector();
    if (Intersector.overlapConvexPolygons(p1, p2, v)) {
      return v;
    }
    return null;
  }

  /**
   * Check collision the position p
   *
   * @param p
   * @param entity
   * @param collidable
   * @return vector to resolve the collision if one happened
   */
  public MinimumTranslationVector checkCollision(Position p, Entity entity, Entity collidable) {
    CollisionBoundary stepBoundary = entity.collisionBoundary().clone();
    stepBoundary.update(p, entity.getRotation(p.x, p.y));
    return checkCollision(stepBoundary, collidable.collisionBoundary());
  }

  /**
   * Check collision between two entities in two different positions
   *
   * @param p1
   * @param entity
   * @param p2
   * @param collidable
   * @return
   */
  public MinimumTranslationVector checkCollision(
      Position p1, Entity entity, Position p2, Entity collidable) {
    CollisionBoundary stepBoundary1 = updatedCollisionBoundary(p1, entity);
    CollisionBoundary stepBoundary2 = updatedCollisionBoundary(p2, collidable);

    return checkCollision(stepBoundary1, stepBoundary2);
  }

  // Get collision boundary of the entity in different position
  private CollisionBoundary updatedCollisionBoundary(Position p, Entity e) {
    CollisionBoundary boundary = e.collisionBoundary().clone();
    boundary.update(p, e.getRotation(p.x, p.y));
    return boundary;
  }

  /**
   * Perform elastic collision on the step using the minimum translation vector
   *
   * @param step
   * @param elastic
   * @param v vector
   * @return
   */
  public Direction collide(Step step, float elastic, MinimumTranslationVector v) {
    Direction colPlace;
    Position pdelta =
        new Position(
            step.position.x + v.normal.x * v.depth, step.position.y + v.normal.y * v.depth);
    PlanetPosition ppdelta = world.convert(pdelta);
    PlanetPosition pp = world.convert(step.position);
    double angleRatio = world.angleRatio(pp.fromCenter);

    // Get direction of the collision
    if (Math.abs(ppdelta.angle - pp.angle)
        > Math.abs(ppdelta.fromCenter - pp.fromCenter) * angleRatio) {

      if (ppdelta.angle < pp.angle) {
        colPlace = Direction.RIGHT;
      } else {
        colPlace = Direction.LEFT;
      }

    } else {

      if (ppdelta.fromCenter < pp.fromCenter) {
        colPlace = Direction.UP;
      } else {
        colPlace = Direction.DOWN;
      }
    }

    double angleNorm = world.getAngleTo(step.position.x, step.position.y);
    Double speedVBefore = step.vector.getValue();

    if (speedVBefore.isNaN()) {
      return colPlace;
    }

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

    return colPlace;
  }
}
