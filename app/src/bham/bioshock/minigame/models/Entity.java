package bham.bioshock.minigame.models;

import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.CollisionBoundary;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.physics.Vector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.Polygon;

public abstract class Entity {

  protected final double GROUND_FRICTION = 0.2;

  protected int width = 50;
  protected int height = 50;
  
  protected Boolean isStatic;
  protected Position pos;
  protected boolean loaded = false;
  protected Sprite sprite;
  protected float rotation;
  protected float fromGround;
  protected SpeedVector speed;
  protected World world;

  protected CollisionBoundary collisionBoundary;
  protected float collisionWidth = 50;
  protected float collisionHeight = 50;

  protected boolean onGround;
  protected State state = State.CREATED;
  private Objective objective = null;
  
  public Entity(World w, float x, float y, boolean isStatic) {
    this.isStatic = isStatic;
    pos = new Position(x, y);
    speed = new SpeedVector();
    fromGround = 0;
    world = w;
    onGround = false;
  }
  
  public Entity(World w, float x, float y) {
    this(w, x, y, false);
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }

  public boolean isRemoved() {
    return state.equals(State.REMOVED);
  }

  public Position getPos() {
    return pos;
  }

  public float getX() {
    return pos.x;
  }

  public float getY() {
    return pos.y;
  }

  public boolean isFlying() {
    return distanceFromGround() > 10 && !onGround;
  }

  public boolean isA(Class<? extends Entity> c) {
    return c.isInstance(this);
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public double distanceFromGround() {
    return world.fromGroundTo(getX(), getY()) - fromGround;
  }

  public double angleToCenterOfGravity() {
    return 180 + angleFromCenter();
  }

  public double getRotation() {
    return rotation - angleFromCenter();
  }

  public double angleFromCenter() {
    return world.getAngleTo(getX(), getY());
  }

  public Objective getObjective(){return this.objective;}
  public void setObjective(Objective o){this.objective = o;}
  public abstract TextureRegion getTexture();

  public void load() {
    this.loaded = true;
    state = State.LOADED;
    if (getTexture() != null) {
      sprite = new Sprite(getTexture());
      sprite.setSize(width, height);
      sprite.setOrigin(sprite.getWidth() / 2, 0);
    }
    collisionBoundary = new CollisionBoundary(collisionWidth, collisionHeight);
    collisionBoundary.update(pos, getRotation());
  }

  public Sprite getSprite() {
    return sprite;
  }

  public void setSpeed(float angle, float force) {
    speed.apply(angle, force);
  }

  public void setSpeedVector(SpeedVector s) {
    speed = s;
  }
  public SpeedVector getSpeedVector() {
    return speed;
  }

  public void update(float delta) {
    if (!loaded || isStatic)
      return;
    double angle = angleToCenterOfGravity();

    pos.y += speed.dY() * delta;
    pos.x += speed.dX() * delta;
    
    if (isFlying()) {
      speed.apply(angle, world.getGravity() * delta);
    } else {
      speed.friction(GROUND_FRICTION);
      speed.stop(angle);
    }

    collisionBoundary.update(pos, getRotation());
  }

  public MinimumTranslationVector checkCollision(Polygon p) {
    MinimumTranslationVector v = new MinimumTranslationVector();
    if (collisionBoundary.collideWith(p, v)) {
      return v;
    }
    return null;
  }
  
  public MinimumTranslationVector checkCollision(Entity e) {
    return checkCollision(e.collisionBoundary);
  }

  /*
   * Default behaviour for the collision. Can be overwritten by the subclass
   */
  public void handleCollision(Entity e) {}

  public CollisionBoundary collisionBoundary() {
    return collisionBoundary;
  }

  public void drawDebug(ShapeRenderer shapeRenderer) {
    collisionBoundary().draw(shapeRenderer, Color.WHITE);
    speed.draw(shapeRenderer, pos);
  }

  public boolean is(State s) {
    return state.equals(s);
  }
  
  public void resetColision() {
    this.onGround = false;
  }
 

  public void collide(float elastic, MinimumTranslationVector v) {
    if (!loaded) return;
    
    Direction colPlace;
    Position pdelta = new Position(getX() + v.normal.x, getY() + v.normal.y);
    PlanetPosition ppdelta = world.convert(pdelta);
    PlanetPosition pp = world.convert(pos);
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
    
    double angleNorm = angleFromCenter();
    double speedVBefore = speed.getValue();
    
    switch (colPlace) {
      case RIGHT:
        speed.stop(angleNorm + 90);
        speed.apply(angleNorm - 90, (speedVBefore - speed.getValue()) * elastic);
        break;
      case LEFT:
        speed.stop(angleNorm - 90);
        speed.apply(angleNorm + 90, (speedVBefore - speed.getValue()) * elastic);
        break;
      case DOWN:
        speed.stop(angleNorm + 180);
        speed.apply(angleNorm, (speedVBefore - speed.getValue()) * elastic);
      case UP:
        speed.stop(angleNorm);
        speed.apply(angleNorm + 180, (speedVBefore - speed.getValue()) * elastic);
      default:
        break;
    }
  }

  public enum State {
    CREATED, LOADED, REMOVED, REMOVING,
  }

}




