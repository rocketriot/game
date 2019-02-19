package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.physics.Gravity;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


public abstract class Entity {

  protected final double GROUND_FRICTION = 0.2;

  protected int size = 50;

  protected Position pos;
  protected boolean loaded = false;
  protected Sprite sprite;
  private float rotation;
  protected float fromGround;
  protected SpeedVector speed;
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
    return size;
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

  public void setRotation(float rotation) {
    this.rotation = rotation;
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
  public void setSpeedVector(SpeedVector s) {
    speed = s;
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

  public void checkCollision(Entity e) {
    if( Intersector.overlaps(getRectangle(), e.getRectangle()) ) {
      handleCollision(e);
    }
  }
  
  /*
   * Default behaviour for the collision.
   * Can be overwritten by the subclass
   */
  public void handleCollision(Entity e) {
//    double speedAngle = speed.getSpeedAngle();
//    speed.stop(speedAngle);
//    speed.apply(-speedAngle, 20);
  }

  // returns rectangle of the sprite
  public Rectangle getRectangle() {
    Rectangle border = sprite.getBoundingRectangle();

    float new_width = border.getWidth() * 0.5f;
    float x = border.getX() + (border.width - new_width) / 2f;
    Rectangle r = new Rectangle(x, border.getY(), new_width, border.height);

    return r;
  }

}
