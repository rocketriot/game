package bham.bioshock.minigame.models;

import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;


public class Bullet extends Entity {
  private static TextureRegion texture;
  public boolean isFired = false;
  public static final int launchSpeed = 1200;
  static Animation<TextureRegion> splash;
  static int FRAMES = 5;
  private float animationTime = 0;
  private Astronaut shooter;

  public Bullet(World w, float x, float y,Astronaut shooter) {
    super(w, x, y);
    this.shooter = shooter;
    rotation = 0;
    fromGround = -10;
    collisionHeight = getHeight() / 2;
  }

  @Override
  public void update(float d) {
    super.update(d);

    setRotation((float) (angleFromCenter() - (speed.getSpeedAngle() + 360) % 360));

    if (is(State.REMOVED)) {
      return;
    }
    if (!isFlying()) {
      state = State.REMOVING;
      speed.stop(speed.getSpeedAngle());
    }
    if (is(State.REMOVING)) {
      animationTime += d;
    }
    if (splash.isAnimationFinished(animationTime)) {
      state = State.REMOVED;

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

  public static void loadTextures() {
    texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/minigame/bullet.png")));

    Texture t = new Texture(Gdx.files.internal("app/assets/minigame/bullet_animation.png"));
    TextureRegion[][] list = TextureRegion.split(t, t.getWidth() / FRAMES, t.getHeight());

    TextureRegion[] frames = new TextureRegion[FRAMES];
    for (int i = 0; i < FRAMES; i++) {
      frames[i] = list[0][i];
    }

    splash = new Animation<TextureRegion>(0.03f, frames);
  }

  public Astronaut getShooter(){return this.shooter;}

  /** Collisions **/
  @Override
  public void handleCollision(Entity e) {    
    if (e.isA(Astronaut.class)) {
      MinimumTranslationVector v = checkCollision(e);
      if(v == null) return;
      
      collide(0.2f, v);
    } else if (e.isA(Rocket.class)) {
      MinimumTranslationVector v = checkCollision(e);
      if(v == null) return;
      
      collide(0.9f, v);
    }
  }
}
