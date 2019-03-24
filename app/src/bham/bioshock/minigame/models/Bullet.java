package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.physics.Step;
import bham.bioshock.minigame.worlds.World;
import java.util.UUID;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;


public class Bullet extends Entity {

  private static final long serialVersionUID = -7192308795772982285L;

  public static final int launchSpeed = 1100;
  private static TextureRegion texture;
  private static Animation<TextureRegion> splash;
  private static int FRAMES = 5;

  public boolean isFired = false;
  private float animationTime = 0;
  private float timeOfLife = 0;
  private UUID shooter;

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

    setRotation((float) (angleFromCenter() - (speed.getSpeedAngle() + 360) % 360));

    if (is(State.REMOVED)) {
      return;
    }
    if (!isFlying(getX(), getY())) {
      setState(State.REMOVING);
      speed.stop(speed.getSpeedAngle());
    }
    if (is(State.REMOVING)) {
      animationTime += d;
    }
    if (splash.isAnimationFinished(animationTime)) {
      this.remove();
    }
  }

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

  public static void createTextures(AssetManager manager) {
    texture = new TextureRegion(manager.get("app/assets/minigame/bullet.png", Texture.class));

    Texture t = manager.get("app/assets/minigame/bullet_animation.png", Texture.class);
    TextureRegion[][] list = TextureRegion.split(t, t.getWidth() / FRAMES, t.getHeight());

    TextureRegion[] frames = new TextureRegion[FRAMES];
    for (int i = 0; i < FRAMES; i++) {
      frames[i] = list[0][i];
    }

    splash = new Animation<TextureRegion>(0.03f, frames);
  }

  public static void loadTextures(AssetManager manager) {
    manager.load("app/assets/minigame/bullet.png", Texture.class);
    manager.load("app/assets/minigame/bullet_animation.png", Texture.class);
  }

  public UUID getShooter() {
    return this.shooter;
  }

  /** Collisions **/
  @Override
  public boolean canCollideWith(Entity e) {
    switch (e.type) {
      case ASTRONAUT:
      case BULLET:
      case ROCKET:
      case PLATFORM:
        return timeOfLife > 0.06f || !e.getId().equals(shooter);
      default:
        return false;
    }
  }

  @Override
  public void handleCollision(Entity e) {
    switch (e.type) {
      case ASTRONAUT:
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

  public static Bullet createForPlayer(World world, Astronaut player) {
    Position pos = player.getPos();
    PlanetPosition pp = world.convert(pos);
    pp.fromCenter += player.getHeight() / 2;

    SpeedVector speed = player.getSpeedVector().copy();

    if (player.getMove().movingRight) {
      pp.angle += world.angleRatio(pp.fromCenter) * 30;
      speed.apply(world.getAngleTo(pos.x, pos.y) + 90, Bullet.launchSpeed);
    } else {
      pp.angle -= world.angleRatio(pp.fromCenter) * 30;
      speed.apply(world.getAngleTo(pos.x, pos.y) - 90, Bullet.launchSpeed);
    }

    Position bulletPos = world.convert(pp);
    Bullet b = new Bullet(world, bulletPos.x, bulletPos.y, player.getId());
    // Apply bullet speed
    b.setSpeedVector(speed);

    return b;
  }

}
