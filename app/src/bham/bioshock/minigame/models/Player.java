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
  private static Animation<TextureRegion> walkGunAnimation;
  private static TextureRegion frontTexture;
  private static TextureRegion frontGunTexture;
  private final double JUMP_FORCE = 700;
  float animationTime;
  private PlayerTexture dir;
  private float v = 700f;
  private boolean haveGun = false;

  public Player(World w, float x, float y) {
    super(w, x, y);
    size = 150;
    animationTime = 0;
    fromGround = -25;
    update(0);
  }

  public Player(World w, Position p) {
    this(w, p.x, p.y);
  }

  public Player(World w) {
    this(w, 0f, 0f);
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

  
  /**
   * Player textures
   **/

  public TextureRegion getTexture() {
    TextureRegion region = getTexture(haveGun);

    if (region.isFlipX()) {
      region.flip(true, false);
    }
    if (dir == PlayerTexture.LEFT) {
      region.flip(true, false);
    }
    return region;
  }

  private TextureRegion getTexture(boolean withGun) {
    if (withGun && dir.equals(PlayerTexture.FRONT)) {
      return frontGunTexture;
    } else if (dir.equals(PlayerTexture.FRONT)) {
      return frontTexture;
    } else if (withGun) {
      return walkGunAnimation.getKeyFrame(animationTime, true);
    }

    return walkAnimation.getKeyFrame(animationTime, true);
  }


  private static TextureRegion[][] splittedTexture(String path) {
    Texture t = new Texture(Gdx.files.internal(path));
    return TextureRegion.split(t, t.getWidth() / FRAMES, t.getHeight());
  }

  private static Animation<TextureRegion> textureToAnimation(TextureRegion[][] list) {

    TextureRegion[] frames = new TextureRegion[FRAMES - 1];
    for (int i = 1; i < FRAMES; i++) {
      frames[i - 1] = list[0][i];
    }

    return new Animation<TextureRegion>(0.1f, frames);
  }

  public static void loadTextures() {
    TextureRegion[][] walkSheet = splittedTexture("app/assets/minigame/astronaut.png");
    TextureRegion[][] walkGunSheet = splittedTexture("app/assets/minigame/astronaut_gun.png");

    frontTexture = walkSheet[0][0];
    frontGunTexture = walkGunSheet[0][0];

    walkAnimation = textureToAnimation(walkSheet);
    walkGunAnimation = textureToAnimation(walkGunSheet);
  }

  public void shoot() {
    // TODO Auto-generated method stub
    
  }
}
