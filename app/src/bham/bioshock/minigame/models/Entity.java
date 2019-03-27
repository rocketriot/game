package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.*;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Entity represents any object that exists in the minigame Is influenced by the gravity and
 * calculates its speed vector and position Can collide with other entities and handle the collision
 * behaviour
 */
public abstract class Entity implements Serializable {

  private static final long serialVersionUID = -8104423419633831645L;

  /** ID of the entity */
  protected UUID id;

  /** Default size */
  protected int width = 50;
  protected int height = 50;

  /** Type of the entity */
  public final EntityType type;
  /** If is static */
  protected final Boolean isStatic;
  /** Current position */
  protected Position pos;
  /** If has been loaded and is ready to draw */
  protected boolean loaded = false;
  /** Sprite used for drawing and rotation */
  protected transient Sprite sprite;
  /** Relative rotation to the ground, is not changed by the position */
  protected float rotation;

  /**
   * Offset for the texture, if the entity needs to be drawn lower or higher in respect to their
   * position
   */
  protected float fromGround;

  /** Current speed vector */
  protected SpeedVector speed;

  /** Current world specification */
  protected World world;

  /** State of the entity */
  private State state = State.CREATED;

  /** Collision boundary for the entity. Used to detect collisions */
  protected transient CollisionBoundary collisionBoundary;
  /** Collision handler for detecting collisions with other entities */
  protected transient CollisionHandler collisionHandler;

  /** Collision boundary size */
  protected float collisionWidth = 50;
  protected float collisionHeight = 50;

  /** Steps generator generating future positions */
  protected transient StepsGenerator stepsGenerator;

  /** Minigame objective, used to notify for events related to objective */
  protected transient Optional<Objective> objective = Optional.empty();

  /**
   * Creates entity with random id, default position and zero speed vector
   * 
   * @param w
   * @param x
   * @param y
   * @param isStatic
   * @param type
   */
  public Entity(World w, float x, float y, boolean isStatic, EntityType type) {
    this.id = UUID.randomUUID();
    this.isStatic = isStatic;
    this.type = type;
    pos = new Position(x, y);
    speed = new SpeedVector();
    fromGround = 0;
    world = w;
    if (!isStatic) {
      stepsGenerator = new StepsGenerator(w, this);
    }
  }

  /**
   * Creates dynamic entity
   * 
   * @see #Entity(World, float, float, boolean, EntityType)
   * 
   * @param w
   * @param x
   * @param y
   * @param type
   */
  public Entity(World w, float x, float y, EntityType type) {
    this(w, x, y, false, type);
  }

  /**
   * Get entity Id
   * 
   * @return id
   */
  public UUID getId() {
    return id;
  }

  /**
   * Save collision handler
   * 
   * @param collisionHandler
   */
  public void setCollisionHandler(CollisionHandler collisionHandler) {
    this.collisionHandler = collisionHandler;
    if (stepsGenerator != null) {
      stepsGenerator.setCollisionHandler(collisionHandler);
    }
  }

  /**
   * Get width
   * 
   * @return width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Get height
   * 
   * @return height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Mark entity as removed
   */
  public void remove() {
    state = State.REMOVED;
    if(stepsGenerator != null) {
      stepsGenerator.stop();      
    }
  }

  /**
   * Update entity state
   * 
   * @param s
   */
  protected void setState(State s) {
    if (s.equals(State.REMOVED)) {
      remove();
    } else {
      state = s;
    }
  }

  /**
   * Check if entity is marked as removed
   */
  public boolean isRemoved() {
    return state.equals(State.REMOVED);
  }

  /**
   * Get current position
   * 
   * @return position
   */
  public Position getPos() {
    return pos;
  }

  /**
   * Get current position relative to the planet center
   * 
   * @return position in polar space
   */
  public PlanetPosition getPlanetPos() {
    return world.convert(getPos());
  }
  
  /**
   * Get current x coordinate
   * 
   * @return
   */
  public float getX() {
    return pos.x;
  }

  /**
   * Get current y coordinate
   * 
   * @return
   */
  public float getY() {
    return pos.y;
  }

  /**
   * Get current step including current position and current speed vector
   * 
   * @return
   */
  public Step currentStep() {
    return new Step(pos, speed);
  }

  /**
   * Check if the entity will be flying in the x, y position
   * 
   * @param x
   * @param y
   * @return
   */
  public boolean isFlying(float x, float y) {
    return distanceFromGround(x, y) > 0;
  }

  /**
   * Check if entity is an instance of a class
   * 
   * @param c
   * @return
   */
  public boolean isA(Class<? extends Entity> c) {
    return c.isInstance(this);
  }

  /**
   * Update rotation relative to the ground
   */
  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  /**
   * Get current distance from the ground
   * 
   * @return distance
   */
  public double distanceFromGround() {
    return distanceFromGround(getX(), getY());
  }

  /**
   * Get distance form the ground in the position x, y
   * 
   * @param x
   * @param y
   * @return distance
   */
  public double distanceFromGround(float x, float y) {
    return world.fromGroundTo(x, y) - fromGround;
  }

  /**
   * Get angle to center of a gravity
   * 
   * @return angle in degrees
   */
  public double angleToCenterOfGravity() {
    return 180 + angleFromCenter();
  }

  /**
   * Get current entity rotation relative to the planet center
   * 
   * @return angle in degrees
   */
  public double getRotation() {
    return getRotation(getX(), getY());
  }

  /**
   * Get entity rotation in position x,y relative to the planet center
   * 
   * @return angle in degrees
   */
  public double getRotation(float x, float y) {
    return rotation - world.getAngleTo(x, y);
  }

  /**
   * Get angle from the center of a gravity
   * 
   * @return angle in degrees
   */
  public double angleFromCenter() {
    return world.getAngleTo(getX(), getY());
  }

  /**
   * Save objective
   * 
   * @param o current objective
   */
  public void setObjective(Objective o) {
    objective = Optional.of(o);
  }

  /**
   * Get texture for rendering
   * 
   * @return texture region
   */
  public abstract TextureRegion getTexture();

  /**
   * Load entity, create a sprite, collision boundary and step generator if entity is not static
   */
  public void load() {
    this.loaded = true;
    setState(State.LOADED);
    if (getTexture() != null) {
      sprite = new Sprite(getTexture());
      sprite.setSize(width, height);
      sprite.setOrigin(sprite.getWidth() / 2, 0);
    }
    collisionBoundary = new CollisionBoundary(collisionWidth, collisionHeight);
    collisionBoundary.update(pos, getRotation());
    if (!isStatic) {
      if (stepsGenerator == null) {
        stepsGenerator = new StepsGenerator(world, this);
      }
      stepsGenerator.generate();
    }
  }

  /**
   * Get sprite
   * 
   * @return sprite
   */
  public Sprite getSprite() {
    return sprite;
  }

  /**
   * Set entity speed
   * 
   * @param angle
   * @param force
   */
  public void setSpeed(float angle, float force) {
    speed.apply(angle, force);
  }

  /**
   * Set current step
   * 
   * @param step
   */
  public void setStep(Step step) {
    speed = step.vector;
    pos = step.position;
  }

  /**
   * Update current speed vector
   * 
   * @param s
   */
  public void setSpeedVector(SpeedVector s) {
    speed = s;
  }

  /**
   * Get current speed vector
   * 
   * @return speed vector
   */
  public SpeedVector getSpeedVector() {
    return speed;
  }

  /**
   * Get future step if one is generated
   * 
   * @param n
   * @return optional step
   */
  public Optional<Step> getFutureStep(int n) {
    return stepsGenerator.getFutureStep(n);
  }

  /**
   * Update entity position by requesting new step from the step generator
   * and then chaning position and speed vector accordingly
   * 
   * @param delta
   */
  public void update(float delta) {
    Step step = null;
    if (loaded && !isStatic) {
      step = stepsGenerator.getStep(delta);
    }

    if (step != null) {
      pos = step.position;
      speed = step.vector;

      for (Entity e : step.getCollisions()) {
        handleCollision(e);
      }
    }

    collisionBoundary.update(pos, getRotation());
  }


  /**
   * Default behaviour for the collision while predicting steps.
   * Should be overridden by the subclass
   */
  public boolean handleCollisionMove(Step step, MinimumTranslationVector v, Entity e) {
    return false;
  }
  
  /**
   * Handle collision with the entity while touching it
   * Should be overriden by the subclass
   * @param e
   */
  public void handleCollision(Entity e) {}

  /**
   * Check if entity can collide with other entity
   * Used to speed up collision check
   * 
   * @param e
   * @return
   */
  public boolean canCollideWith(Entity e) {
    return false;
  }

  /**
   * Get entity collision boundary
   * 
   * @return
   */
  public CollisionBoundary collisionBoundary() {
    return collisionBoundary;
  }
  
  /**
   * Draw debug lines and step prediction
   * 
   * @param shapeRenderer
   */
  public void drawDebug(ShapeRenderer shapeRenderer) {
    collisionBoundary().draw(shapeRenderer, Color.WHITE);
    if (isStatic || !loaded)
      return;
    speed.draw(shapeRenderer, pos);
    Stream<Step> futureSteps = stepsGenerator.getFutureSteps();

    shapeRenderer.begin(ShapeType.Filled);
    futureSteps.forEach(step -> {
      shapeRenderer.circle(step.position.x, step.position.y, 5);
    });
    shapeRenderer.end();
  }
  
  /**
   * Check the state of the entity
   * 
   * @param s
   * @return
   */
  public boolean is(State s) {
    return state.equals(s);
  }
  
  /**
   * Draw the entity
   * 
   * @param batch
   */
  public void draw(SpriteBatch batch) {
    TextureRegion texture = getTexture();
    if (texture == null)
      return;
    Sprite sprite = getSprite();
    sprite.setRegion(texture);
    sprite.setPosition(getX() - (sprite.getWidth() / 2), getY());
    sprite.setRotation((float) getRotation());
    sprite.draw(batch);
  }
  
  /**
   * After drawing, used to render add additional stuff on the top of the entity
   * 
   * @param batch
   */
  public void afterDraw(SpriteBatch batch) {};


  /**
   * Returns step generator
   * @return step generator
   */
  public StepsGenerator getStepsGenerator() {
    return stepsGenerator;
  }

  /**
   * Checks if the entity is loaded
   * @return
   */
  public boolean loaded() {
    return loaded;
  }
  
  /** Possible entity states */
  public enum State {
    CREATED, LOADED, REMOVED, REMOVING,
  }
}

