package bham.bioshock.minigame.models;

import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.physics.CollisionBoundary;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Entity {

  protected final double GROUND_FRICTION = 0.2;

  protected int size = 50;

  protected Position pos;
  protected boolean loaded = false;
  protected Sprite sprite;
  protected float rotation;
  protected float fromGround;
  protected SpeedVector speed;
  private World world;
  protected CollisionBoundary collisionBoundary;
  protected float collisionWidth = 50;
  protected float collisionHeight = 50;

  protected State state = State.CREATED;

  public Entity(World w, float x, float y) {
    pos = new Position(x, y);
    speed = new SpeedVector();
    fromGround = 0;
    world = w;
  }

  public int getSize() {
    return size;
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
    return distanceFromGround() > 10;
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


  public abstract TextureRegion getTexture();

  public void load() {
    this.loaded = true;
    state = State.LOADED;
    if (getTexture() != null) {
      sprite = new Sprite(getTexture());
      sprite.setSize(getSize(), getSize());
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

  public void update(float delta) {
    if (!loaded)
      return;
    double angle = angleToCenterOfGravity();

    pos.y += speed.dY() * delta;
    pos.x += speed.dX() * delta;

    if (isFlying()) {
      speed.apply(angle, world.getGravity() * delta);
    }
    if (!isFlying()) {
      speed.friction(GROUND_FRICTION);
      speed.stop(angle);
    }

    collisionBoundary.update(pos, getRotation());
  }

  public boolean checkCollision(Entity e) {
    if (collisionBoundary.collideWith(e.collisionBoundary)) {
      return true;
    }
    return false;
  }

  /*
   * Default behaviour for the collision. Can be overwritten by the subclass
   */
  public void handleCollision(Entity e) {

  }

  public CollisionBoundary collisionBoundary() {
    return collisionBoundary;
  }

  public void drawDebug(ShapeRenderer shapeRenderer) {
    collisionBoundary().draw(shapeRenderer);
    speed.draw(shapeRenderer, pos);
  }

  public boolean is(State s) {
    return state.equals(s);
  }

  public void collide(Entity e, float elastic) {
    if (!loaded)
      return;
    Direction colPlace = collisionBoundary.getDirectionTo(world, e.collisionBoundary());
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
      case UP:
        speed.stop(angleNorm);
        speed.apply(angleNorm + 180, (speedVBefore - speed.getValue()) * elastic);
      case DOWN:
        speed.stop(angleNorm + 180);
        speed.apply(angleNorm, (speedVBefore - speed.getValue()) * elastic);
      default:
        break;
    }
  }

  public enum State {
    CREATED, LOADED, REMOVED, REMOVING,
  }

}
