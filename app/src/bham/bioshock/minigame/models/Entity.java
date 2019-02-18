package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.physics.Gravity;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;


public abstract class Entity {

  protected final double GROUND_FRICTION = 0.2;
  private final double AIR_FRICTION = 0.001;

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


  public void col(float delta) {
    double angle = angleToCenterOfGravity();
    double angleFromCenter = angleFromCenter();
    speed.stop(angleFromCenter);

  }

  // returns rectangle of the sprite
  public Rectangle getRectangle() {
    // Rectangle r = new Rectangle(sprite.getBoundingRectangle().getX(),
    // sprite.getBoundingRectangle().getY(), sprite.getBoundingRectangle().getWidth()/2.0f,
    // sprite.getBoundingRectangle().getHeight(),)
    Rectangle border = sprite.getBoundingRectangle();
    // System.out.println(border);
    float new_width = border.getWidth() * 0.5f;
    float x = border.getX() + (border.width - new_width) / 2f;
    Rectangle r = new Rectangle(x, border.getY(), new_width, border.height);
    // System.out.println(r);
    return r;
  }

}
