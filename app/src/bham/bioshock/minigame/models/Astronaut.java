package bham.bioshock.minigame.models;

import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.communication.Sendable;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.models.Entity.State;
import bham.bioshock.minigame.physics.CollisionBoundary;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.physics.Step;
import bham.bioshock.minigame.worlds.World;
import static java.util.stream.Collectors.toList;

public class Astronaut extends Entity {

  private static final long serialVersionUID = -5131439342109870021L;
  private static Animation<TextureRegion> walkAnimation;
  private static Animation<TextureRegion> walkGunAnimation;
  private static TextureRegion frontTexture;
  private static TextureRegion frontGunTexture;
  float animationTime;
  private PlayerTexture dir = PlayerTexture.FRONT;
  private boolean haveGun = false;
  private CollisionBoundary legs;
  private String name;
  private Move move = new Move();
  private Position respawn;
  static Animation<TextureRegion> dyingFront;
  static Animation<TextureRegion> dyingBack;
  private boolean dieFront = true;
  private float dieTime;
  private Direction shotDirection;


  public Astronaut(World w, float x, float y) {
    super(w, x, y, EntityType.ASTRONAUT);
    width = 150;
    height = 150;
    animationTime = 0;
    fromGround = -20;
    update(0);
    collisionWidth = 65;
    collisionHeight = 150 + fromGround;
  }

  public Astronaut(World w, Position p) {
    this(w, p.x, p.y);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void moveLeft(boolean value) {
    move.movingLeft = value;
  }

  public void moveRight(boolean value) {
    move.movingRight = value;
  }

  public void jump(boolean value) {
    if (value) {
      SoundController.playSound("jump");
    }
    move.jumping = value;
  }

  public List<Step> getFutureSteps() {
    return stepsGenerator.getFutureSteps().collect(toList());
  }

  public void moveChange() {
    if (move.movingLeft) {
      dir = PlayerTexture.LEFT;
      stepsGenerator.moveLeft();
    } else if (move.movingRight) {
      dir = PlayerTexture.RIGHT;
      stepsGenerator.moveRight();
    } else {
      stepsGenerator.moveStop();
      dir = PlayerTexture.FRONT;
    }
    stepsGenerator.jump(move.jumping);
  }

  public void resetDirection() {
    dir = PlayerTexture.FRONT;
  }

  public boolean haveGun() {
    return haveGun;
  }

  public void setGun(Boolean b) {
    this.haveGun = b;
  }

  public void update(float delta) {
    if (!loaded)
      return;
    super.update(delta);
    legs.update(pos, getRotation());
    animationTime += delta;
  }

  public Move getMove() {
    return move;
  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer) {
    super.drawDebug(shapeRenderer);
    legs.draw(shapeRenderer, Color.MAGENTA);
  }

  @Override
  public void draw(SpriteBatch batch, float delta) {
    if (is(State.REMOVING)) {
      TextureRegion anim;
      if(dieFront) {
        anim = dyingFront.getKeyFrame(dieTime, false);        
      } else {
        anim = dyingBack.getKeyFrame(dieTime, false);
      }
//      setRotation( (dieTime / 0.71f)*90 );
      collisionBoundary.update(respawn, delta);
      collisionBoundary.update(pos, getRotation() - (dieTime / 0.71f)*90);
      Sprite sprite = getSprite();
      sprite.setRegion(anim);
      sprite.setPosition(getX() - (sprite.getWidth() / 2), getY());
      sprite.setRotation((float) getRotation());
      sprite.draw(batch);

      if (dieFront && dyingFront.isAnimationFinished(dieTime)
          || dyingBack.isAnimationFinished(dieTime)) {
        System.out.println(dieTime);
        state = State.LOADED;
        setRotation(0);
        stepsGenerator.updateFromServer(new SpeedVector(), respawn);
      }
      dieTime += delta;
    } else {
      super.draw(batch, delta);
    }
  }

  public void setPosition(Position p) {
    pos = p;
    if (collisionBoundary != null) {
      collisionBoundary.update(pos, getRotation());
    }
    if (legs != null) {
      legs.update(pos, getRotation());
    }
  }


  public void load() {
    super.load();
    legs = new CollisionBoundary(collisionWidth + 10, collisionHeight / 10);
    legs.update(pos, getRotation());
  }

  /**
   * Player textures
   **/

  public TextureRegion getTexture() {
    TextureRegion region = getTexture(haveGun);
    if (region == null)
      return null;
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


  private static TextureRegion[][] splittedTexture(String path, int fnum) {
    Texture t = new Texture(Gdx.files.internal(path));
    return TextureRegion.split(t, t.getWidth() / fnum, t.getHeight());
  }

  private static Animation<TextureRegion> textureToAnimation(TextureRegion[][] list, int fnum, int skip, float duration) {

    TextureRegion[] frames = new TextureRegion[fnum - skip];
    for (int i = skip; i < fnum; i++) {
      frames[i - skip] = list[0][i];
    }

    return new Animation<TextureRegion>(duration, frames);
  }

  @Override
  public boolean canColideWith(Entity e) {
    switch (e.type) {
      case ASTRONAUT:
      case BULLET:
      case GUN:
      case PLATFORM:
      case FLAG:
        return true;
      default:
        return false;
    }
  }

  /** Collisions **/
  @Override
  public void handleCollision(Entity e) {
    if(e.is(State.REMOVING) || !e.loaded) return;
    switch (e.type) {
      case GUN:
        if(!haveGun()) {
          haveGun = true;
          e.remove();          
        }
        break;
      case BULLET:
        getObjective().gotShot(this, ((Bullet) e).getShooter());  
        break;
      case FLAG:
        this.getObjective().captured(this);
        e.state = State.REMOVED;
        break;
      default:
        break;
    }
  }

  @Override
  public boolean handleCollisionMove(Step step, MinimumTranslationVector v, Entity e) {
    switch (e.type) {
      case BULLET:
        if (e.state.equals(State.REMOVING)) return false;
        collisionHandler.collide(step, 0.2f, v);
        return false;
      case ASTRONAUT:
        collisionHandler.collide(step, .8f, v);
        return true;
      case ROCKET:
        collisionHandler.collide(step, .3f, v);
        return true;
      case PLATFORM:
        // Standard collision fix
        step.position.x += v.normal.x * v.depth;
        step.position.y += v.normal.y * v.depth;

        collisionHandler.collide(step, 0f, v);

        // Check collision with legs
        MinimumTranslationVector vlegs = new MinimumTranslationVector();
        CollisionBoundary legs = this.legs.clone();
        legs.update(step.position, this.getRotation(step.position.x, step.position.y));

        if (legs.collideWith(e.collisionBoundary, vlegs)) {
          // Standing on the platform
          step.setOnGround(true);
        }
        return false;
      default:
        return false;
    }
  }


  public static void loadTextures() {
    TextureRegion[][] walkSheet = splittedTexture("app/assets/minigame/astronaut.png", 11);
    TextureRegion[][] walkGunSheet = splittedTexture("app/assets/minigame/astronaut_gun.png", 11);
    TextureRegion[][] dieFront = splittedTexture("app/assets/minigame/die_front.png", 10);
    TextureRegion[][] dieBack = splittedTexture("app/assets/minigame/die_back.png", 12);

    frontTexture = walkSheet[0][0];
    frontGunTexture = walkGunSheet[0][0];

    walkAnimation = textureToAnimation(walkSheet, 11, 1, 0.1f);
    walkGunAnimation = textureToAnimation(walkGunSheet, 11, 1, 0.1f);
    dyingFront = textureToAnimation(dieFront, 10, 0, 0.05f);
    dyingBack = textureToAnimation(dieBack, 12, 0, 0.05f);
  }

  public void updateFromServer(SpeedVector speed, Position pos, Move move, Boolean haveGun) {
    this.haveGun = haveGun;
    this.move = move;
    this.moveChange();
    stepsGenerator.updateFromServer(speed, pos);
  }

  public static class Move extends Sendable {

    private static final long serialVersionUID = 3668803304780843571L;
    public boolean jumping = false;
    public boolean movingLeft = false;
    public boolean movingRight = false;
  }

  public void killAndRespawn(Position pos) {
    dieTime = 0;
    dieFront = true;
    if(shotDirection != null && shotDirection == Direction.LEFT) {
      if(dir == PlayerTexture.LEFT) {
        dieFront = false;
      }
    } else {
      if(dir == PlayerTexture.RIGHT) {
        dieFront = false;
      }
    }
    
    haveGun = false;
    state = State.REMOVING;
    this.respawn = pos;
  }

}
