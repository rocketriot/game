package bham.bioshock.minigame.models;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.minigame.worlds.World.PlanetPosition;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;

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
  private float health = 0;
  private float kills = 0;

  public Player(World w, float x, float y) {
    super(w, x, y);
    width = 150;
    height = 150;
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
    if (!isFlying() && speed.getValueFor(angleFromCenter()) < JUMP_FORCE * 3/4 ) {
      speed.apply(angleFromCenter(), JUMP_FORCE);
    }
  }
  
  public boolean haveGun() {
    return haveGun;
  }
  public void setGun(Boolean b) {
    this.haveGun = b;
  }

  public void update(float delta) {
    super.update(delta);
    animationTime += delta;
    dir = PlayerTexture.FRONT;
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
    collisionBoundary.update(pos, getRotation());
  }


  /**
   * Player textures
   **/

  public TextureRegion getTexture() {
    TextureRegion region = getTexture(haveGun);
    if(region == null) return null;
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

  /** Collisions **/
  @Override
  public void handleCollision(Entity e, MinimumTranslationVector v) {
    if(e.isA(Bullet.class)) {
      collide(.2f, v);
    } else if(e.isA(Player.class) || e.isA(Rocket.class)) {
      collide(0.8f, v);
    } else if(e.isA(Gun.class)) {
      e.state = State.REMOVED;
      haveGun = true;
    } else if(e.isA(StaticEntity.class)) {
      super.onGround = true;
      pos.x += v.normal.x;
      pos.y += v.normal.y;
      
      collide(0f, v);
    }
  }


  public void setHealth(float newHealth){
    this.health = newHealth;
  }

  public float getHealth(){
    return this.health;
  }

  public void addKills(float addedKills){
    this.kills += addedKills;
  }

  public float getKills(){
    return this.kills;
  }
}
