package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.physics.Gravity;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Entity {

  protected final double GROUND_FRICTION = 0.2;
  private final double AIR_FRICTION = 0.001;
  protected int SIZE = 50;
  protected Position pos;
  protected boolean loaded = false;
  protected Sprite sprite;
  protected float fromGround;
  protected SpeedVector speed;
  private float rotation;
  private World world;
  private Gravity gravity;

  public Entity(World w, float x, float y) {
    pos = new Position(x, y);
    gravity = new Gravity(w);
    speed = new SpeedVector();
    fromGround = 0;
    world = w;
  }

  public Entity(World w) {
    this(w, 0f, 0f);
  }

  public int getSize() {
    return SIZE;
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

  public double distanceFromGround() {
    double dx = getX() - world.gravityCenter().x;
    double dy = getY() - world.gravityCenter().y;

    double toCenter = Math.sqrt(dx * dx + dy * dy);
    return toCenter - (world.getPlanetRadius() + fromGround);
  }

  public double angleToCenterOfGravity() {
    return 180 + angleFromCenter();
  }

  public double getRotation() {
    return -angleFromCenter() + rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public double angleFromCenter() {
    return gravity.getAngleTo(getX(), getY());
  }

  public abstract TextureRegion getTexture();

  public void load() {
    sprite = new Sprite(getTexture());
    sprite.setSize(getSize(), getSize());
    sprite.setOrigin(0, 0);
  }

  public Sprite getSprite() {
    return sprite;
  }

  public void setSpeed(float angle, float force) {
    speed.apply(angle, force);
  }

  public void update(float delta) {
    double angle = angleToCenterOfGravity();
    double angleFromCenter = angleFromCenter();

    pos.y += speed.dY() * delta;
    pos.x += speed.dX() * delta;

    if (isFlying()) {
      speed.apply(angle, world.getGravity() * delta);
    } else {
      speed.stop(angleFromCenter);
    }

    if (!isFlying()) {
      speed.friction(GROUND_FRICTION);
    }
  }
}
