package bham.bioshock.minigame.models;
import java.util.stream.Stream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.physics.CollisionBoundary;
import bham.bioshock.minigame.physics.Step;
import bham.bioshock.minigame.worlds.World;

public class Astronaut extends Entity {

  private static final int FRAMES = 11;
  private static Animation<TextureRegion> walkAnimation;
  private static Animation<TextureRegion> walkGunAnimation;
  private static TextureRegion frontTexture;
  private static TextureRegion frontGunTexture;
  float animationTime;
  private PlayerTexture dir = PlayerTexture.FRONT;
  private boolean haveGun = false;
  private CollisionBoundary legs;
  private boolean movingLeft = false;
  private boolean movingRight = false;


  public Astronaut(World w, float x, float y) {
    super(w, x, y, EntityType.PLAYER);
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

  public void moveLeft(boolean value) {
    movingLeft = value;
  }
  
  public void moveRight(boolean value) {
    movingRight = value;
  }

  public void jump(boolean value) {
    SoundController.playSound("jump");
    stepsGenerator.jump(value);
  }
  
  public void moveChange() {
    if(movingRight) {
      dir = PlayerTexture.RIGHT;
      stepsGenerator.moveRight();
      return;
    } else if(movingLeft) {
      dir = PlayerTexture.LEFT; 
      stepsGenerator.moveLeft();
      return;
    }
    stepsGenerator.moveStop();
    dir = PlayerTexture.FRONT;  
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
    if(!loaded) return;
    super.update(delta);
    legs.update(pos, getRotation());
    animationTime += delta;
  }

  public PlayerTexture getDirection() {
    return dir;
  }

  public void setDirection(PlayerTexture t) {
    dir = t;
  }
  
  @Override
  public void drawDebug(ShapeRenderer shapeRenderer) {
    super.drawDebug(shapeRenderer);
    legs.draw(shapeRenderer, Color.MAGENTA);     
  }

  public void setPosition(Position p) {
    pos = p;
    collisionBoundary.update(pos, getRotation());
    legs.update(pos, getRotation());
  }

  
  public void load() {
    super.load();
    legs = new CollisionBoundary(collisionWidth+10, collisionHeight / 10);
    legs.update(pos, getRotation());
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

  @Override
  public boolean canColideWith(Entity e) {
    switch(e.type) {
      case PLAYER:
      case BULLET:
      case ROCKET:
      case GUN:
      case PLATFORM:
        return true;
      default:
        return false;
    }
  }
  
  /** Collisions **/
  @Override
  public void handleCollision(Entity e) {
    switch(e.type) {
      case GUN:
        haveGun = true;
        e.remove();
        break;
      case BULLET:
        getObjective().gotShot(this, ((Bullet) e).getShooter());
        break;
      default:
        break;
    }
  }
  
  @Override
  public void handleCollisionMove(Step step, MinimumTranslationVector v, Entity e) {
    switch(e.type) { 
      case BULLET:
        collisionHandler.collide(step, 0.2f, v);
        break;
      case PLAYER:
        collisionHandler.collide(step, .8f, v);
        break;
      case ROCKET:
        collisionHandler.collide(step, .3f, v);
        break;
      case PLATFORM:
        // Standard collision check
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
        break;
      default:
        break;
    }
  }

  
  public static void loadTextures() {
    TextureRegion[][] walkSheet = splittedTexture("app/assets/minigame/astronaut.png");
    TextureRegion[][] walkGunSheet = splittedTexture("app/assets/minigame/astronaut_gun.png");

    frontTexture = walkSheet[0][0];
    frontGunTexture = walkGunSheet[0][0];

    walkAnimation = textureToAnimation(walkSheet);
    walkGunAnimation = textureToAnimation(walkGunSheet);
  }

}
