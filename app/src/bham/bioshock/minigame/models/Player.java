package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Entity {

  private static final int FRAMES = 11;
  private static Animation<TextureRegion> walkAnimation;
  private static TextureRegion frontTexture;
  private final double JUMP_FORCE = 700;
  float animationTime;
  private PlayerTexture dir;
  private float v = 700f;

  public Player(World w, float x, float y) {
    super(w, x, y);
    size = 150;
    animationTime = 0;
    fromGround = -25;
    update(0);
    collisionWidth = 75;
    collisionHeight = 150;
  }
  
  public Player(World w, Position p) {
    this(w, p.x, p.y);
  }

  public Player(World w) {
    this(w, 0f, 0f);
  }

  public static void loadTextures() {
    Texture walkSheet = new Texture(Gdx.files.internal("app/assets/minigame/astronaut.png"));

    TextureRegion[][] tmp =
        TextureRegion.split(walkSheet, walkSheet.getWidth() / FRAMES, walkSheet.getHeight());

    TextureRegion[] walkFrames = new TextureRegion[FRAMES - 1];
    int index = 0;
    frontTexture = tmp[0][0];
    for (int i = 1; i < FRAMES; i++) {
      walkFrames[index++] = tmp[0][i];
    }

    walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);
  }

  public void moveLeft(float delta) {
    if (!isFlying()) {
      speed.apply(angleFromCenter() + 270, v * GROUND_FRICTION);
    }
    dir = PlayerTexture.LEFT;
  }

  public void moveRight(float delta) {
    if (!isFlying()) {
      speed.apply(angleFromCenter() + 90, v * GROUND_FRICTION);
    }
    dir = PlayerTexture.RIGHT;
  }

  public void jump(float delta) {
    if (!isFlying()) {
      speed.apply(angleFromCenter(), JUMP_FORCE);
    }
  }

  public void update(float delta) {
    super.update(delta);
    animationTime += delta;
    dir = PlayerTexture.FRONT;
  }
  
  public SpeedVector getSpeedVector() {
    return speed;
  }
  public void setSpeedVector(SpeedVector s) {
    speed = s;
  }
  
  public PlayerTexture getDirection() {
    return dir;
  }
  public void setDirection(PlayerTexture t) {
    dir = t;
  }
  
  public Position getPosition() {
    return pos;
  }
  public void setPosition(Position p) {
    pos = p;
  }

  public TextureRegion getTexture() {
    if (dir == PlayerTexture.FRONT) {
      return frontTexture;
    }
    TextureRegion region = walkAnimation.getKeyFrame(animationTime, true);
    if (region.isFlipX()) {
      region.flip(true, false);
    }
    if (dir == PlayerTexture.LEFT) {
      region.flip(true, false);
    }
    return region;
  }
  
  /** Collisions **/
  @Override
  public void handleCollision(Entity e) {

  }
  
}
