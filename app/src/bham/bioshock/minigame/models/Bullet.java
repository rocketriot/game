package bham.bioshock.minigame.models;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.assets.Assets.GamePart;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.physics.Step;
import bham.bioshock.minigame.worlds.World;
import java.util.UUID;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;

/**
 * Bullets are created when a player with a gun is shooting
 * They are usually travelling with high speed
 */
public class Bullet extends Entity {

  private static final long serialVersionUID = -7192308795772982285L;
  
  /** Initial launch speed */
  public static final int launchSpeed = 1100;
  /** Texture for rendering */
  private static TextureRegion texture;
  /** Splash animation displayed when the bullet collide with a player or with a ground */
  private static Animation<TextureRegion> splash;
  /** Number of frames of the animation */
  private static int FRAMES = 5;
  
  private boolean detected = false;
  
  /** Current animation time */
  private float animationTime = 0;
  /** time of life */
  private float timeOfLife = 0;
  /** shooter id */
  private final UUID shooter;

  /**
   * Creates a bullet with initial position and assigned shooter ID
   * 
   * @param w
   * @param x
   * @param y
   * @param shooter
   */
  public Bullet(World w, float x, float y, UUID shooter) {
    super(w, x, y, EntityType.BULLET);
    this.shooter = shooter;
    rotation = 0;
    fromGround = -10;
    collisionHeight = getHeight() / 2;
  }

  @Override
  public void update(float d) {
    super.update(d);
    timeOfLife += d;
    
    // Update rotation according to speed vector
    setRotation((float) (angleFromCenter() - (speed.getSpeedAngle() + 360) % 360));

    if (is(State.REMOVED)) {
      return;
    }
    
    // Start removing when hit the ground
    if (!isFlying(getX(), getY())) {
      setState(State.REMOVING);
      speed.stop(speed.getSpeedAngle());
    }
    
    // Display splash animations
    if (is(State.REMOVING)) {
      animationTime += d;
    }
    
    // If animation is complete mark as removed
    if (splash.isAnimationFinished(animationTime)) {
      remove();
    }
  }

  @Override
  public void load() {
    super.load();
    sprite.setOrigin(sprite.getWidth() / 2, 0);
  }

  @Override
  public TextureRegion getTexture() {
    TextureRegion region;

    if (is(State.REMOVING)) {
      region = splash.getKeyFrame(animationTime, false);
    } else {
      region = texture;
    }

    if (region.isFlipX()) {
      region.flip(true, false);
    }

    double angle = angleFromCenter();

    if (speed.getSpeedAngle() < angle || speed.getSpeedAngle() > angle + 180) {
      region.flip(true, false);
    }

    return region;
  }

  /**
   * Create textures for rendering
   * 
   * @param manager
   */
  public static void createTextures(AssetContainer manager) {
    texture = new TextureRegion(manager.get(Assets.bullet, Texture.class));

    Texture t = manager.get(Assets.bulletAnim, Texture.class);
    TextureRegion[][] list = TextureRegion.split(t, t.getWidth() / FRAMES, t.getHeight());

    TextureRegion[] frames = new TextureRegion[FRAMES];
    for (int i = 0; i < FRAMES; i++) {
      frames[i] = list[0][i];
    }

    splash = new Animation<TextureRegion>(0.03f, frames);
  }

  /**
   * Queue textures for loading
   * 
   * @param manager
   */
  public static void loadTextures(AssetContainer manager) {
    manager.load(Assets.bullet, Texture.class, GamePart.MINIGAME);
    manager.load(Assets.bulletAnim, Texture.class, GamePart.MINIGAME);
  }

  /**
   * Get shooter Id
   * 
   * @return id of the shooter
   */
  public UUID getShooter() {
    return this.shooter;
  }

  @Override
  public boolean canCollideWith(Entity e) {
    switch (e.type) {
      case ASTRONAUT:
      case BULLET:
      case ROCKET:
      case PLATFORM:
        // ignore if just created
        return timeOfLife > 0.06f || !e.getId().equals(shooter);
      default:
        return false;
    }
  }

  @Override
  public void handleCollision(Entity e) {
    switch (e.type) {
      case ASTRONAUT:
        // delegate collision detection to the astronaut class 
        e.handleCollision(this);
        break;
      default:
        break;
    }
  }

  @Override
  public boolean handleCollisionMove(Step step, MinimumTranslationVector v, Entity e) {
    if (e.is(State.REMOVING))
      return false;
    
    switch (e.type) {
      case BULLET:
        // Bullets can collide with itself
        if (!e.is(State.REMOVING)) {
          collisionHandler.collide(step, 1f, v);
        }
        return false;
      case ASTRONAUT:
        return true;
      case PLATFORM:
        collisionHandler.collide(step, .2f, v);
        return true;
      case ROCKET:
        collisionHandler.collide(step, .9f, v);
        return true;
      default:
        break;
    }
    return false;
  }

  /**
   * Spawn new bullet for a player shooting it
   * Adjust the speed vector accordingly
   * 
   * @param world
   * @param player
   * @return
   */
  public static Bullet createForPlayer(World world, Astronaut player) {
    Position pos = player.getPos();
    PlanetPosition pp = world.convert(pos);
    pp.fromCenter += player.getHeight() / 2;

    SpeedVector speed = player.getSpeedVector().copy();
    
    if (player.getMove().movingRight) {
      // Player is moving right - shoot right
      pp.angle += world.angleRatio(pp.fromCenter) * 30;
      speed.apply(world.getAngleTo(pos.x, pos.y) + 90, Bullet.launchSpeed);
    } else {
      // Otherwise shoot left 
      pp.angle -= world.angleRatio(pp.fromCenter) * 30;
      speed.apply(world.getAngleTo(pos.x, pos.y) - 90, Bullet.launchSpeed);
    }

    Position bulletPos = world.convert(pp);
    Bullet b = new Bullet(world, bulletPos.x, bulletPos.y, player.getId());
    // Apply bullet speed
    b.setSpeedVector(speed);

    return b;
  }

  /**
   * Check if the bullets where detected
   * 
   * @return
   */
  public boolean notDetected() {
    if(!detected) {
      detected = true;
      return true;
    }
    return false;
  }

}
