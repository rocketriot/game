package bham.bioshock.minigame.models;

import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.physics.*;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.minigame.worlds.World.PlanetPosition;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.Polygon;

public abstract class Entity {

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
 
  protected StepsGenerator stepsGenerator;

  protected boolean onGround;
  protected State state = State.CREATED;
  
  public Entity(World w, float x, float y, boolean isStatic) {
    this.isStatic = isStatic;
    pos = new Position(x, y);
    speed = new SpeedVector();
    fromGround = 0;
    world = w;
    onGround = false;
    stepsGenerator = new StepsGenerator(w, this);
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
  
  public Step currentStep() {
    return new Step(pos, speed);
  }

//  public boolean isFlying() {
//    return distanceFromGround() > 10 && !onGround;
//  }
  
  public boolean isFlying(float x, float y) {
    return distanceFromGround(x, y) > 0;
  }

  public boolean isA(Class<? extends Entity> c) {
    return c.isInstance(this);
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public double distanceFromGround() {
    return distanceFromGround(getX(), getY());
  }
  
  public double distanceFromGround(float x, float y) {
    return world.fromGroundTo(x, y) - fromGround;
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
    stepsGenerator.generate();
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
    if (!loaded || isStatic) return;
    Step step = stepsGenerator.getStep(delta);
    if(step != null) {
      pos = step.position;
      speed = step.vector;
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

  public void afterCollision() {
//    stepsGenerator.regenerate();
  }
  
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
  
  public void draw(SpriteBatch batch, float delta) {
    Sprite sprite = getSprite();
    sprite.setRegion(getTexture());
    sprite.setPosition(getX() - (sprite.getWidth() / 2), getY());
    sprite.setRotation((float) getRotation());
    sprite.draw(batch);
    update(delta);
  }

  public enum State {
    CREATED, LOADED, REMOVED, REMOVING,
  }

}




