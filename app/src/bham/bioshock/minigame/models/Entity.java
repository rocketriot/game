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

public abstract class Entity implements Serializable {

  private static final long serialVersionUID = 7916524444980988734L;

  /** ID of the entity */
  protected UUID id;

  protected int width = 50;
  protected int height = 50;

  public final EntityType type;
  protected final Boolean isStatic;
  protected Position pos;
  protected boolean loaded = false;
  protected Sprite sprite;
  protected float rotation;
  protected float fromGround;
  protected SpeedVector speed;
  protected World world;

  protected transient CollisionBoundary collisionBoundary;
  protected transient CollisionHandler collisionHandler;
  protected float collisionWidth = 50;
  protected float collisionHeight = 50;

  protected transient StepsGenerator stepsGenerator;

  private State state = State.CREATED;
  protected transient Optional<Objective> objective = Optional.empty();

  public Entity(World w, float x, float y, boolean isStatic, EntityType type) {
    this.id = UUID.randomUUID();
    this.isStatic = isStatic;
    this.type = type;
    pos = new Position(x, y);
    speed = new SpeedVector();
    fromGround = 0;
    world = w;
    stepsGenerator = new StepsGenerator(w, this);
  }

  public Entity(World w, float x, float y, EntityType type) {
    this(w, x, y, false, type);
  }

  public UUID getId() {
    return id;
  }

  public void setCollisionHandler(CollisionHandler collisionHandler) {
    this.collisionHandler = collisionHandler;
    if (stepsGenerator != null) {
      stepsGenerator.setCollisionHandler(collisionHandler);
    }
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void remove() {
    state = State.REMOVED;
    stepsGenerator.stop();
  }
  
  protected void setState(State s) {
    if(s.equals(State.REMOVED)) {
      remove();
    } else {
      state = s;      
    }
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
    return getRotation(getX(), getY());
  }

  public double getRotation(float x, float y) {
    return rotation - world.getAngleTo(x, y);
  }

  public double angleFromCenter() {
    return world.getAngleTo(getX(), getY());
  }

  public void setObjective(Objective o) {
    objective = Optional.of(o);
  }

  public abstract TextureRegion getTexture();

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

  public Sprite getSprite() {
    return sprite;
  }

  public void setSpeed(float angle, float force) {
    speed.apply(angle, force);
  }

  public void setStep(Step step) {
    speed = step.vector;
    pos = step.position;
  }

  public void setSpeedVector(SpeedVector s) {
    speed = s;
  }

  public SpeedVector getSpeedVector() {
    return speed;
  }

  public Optional<Step> getFutureStep(int n) {
    return stepsGenerator.getFutureStep(n);
  }

  public void update(float delta) {
    Step step = null;
    if (loaded && !isStatic) {
      step = stepsGenerator.getStep(delta);;
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


  /*
   * Default behaviour for the collision. Can be overwritten by the subclass
   */
  public boolean handleCollisionMove(Step step, MinimumTranslationVector v, Entity e) {
    return false;
  }

  public void handleCollision(Entity e) {};

  public boolean canColideWith(Entity e) {
    return false;
  }

  public CollisionBoundary collisionBoundary() {
    return collisionBoundary;
  }

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

  public boolean is(State s) {
    return state.equals(s);
  }

  public PlanetPosition getPlanetPos() {
    return world.convert(getPos());
  }


  public void draw(SpriteBatch batch) {
    Sprite sprite = getSprite();
    sprite.setRegion(getTexture());
    sprite.setPosition(getX() - (sprite.getWidth() / 2), getY());
    sprite.setRotation((float) getRotation());
    sprite.draw(batch);
  }

  public void afterDraw(SpriteBatch batch) {};

  public enum State {
    CREATED, LOADED, REMOVED, REMOVING,
  }

  public StepsGenerator getStepsGenerator() {
    return stepsGenerator;
  }

  public boolean loaded() {
    return loaded;
  }
}


