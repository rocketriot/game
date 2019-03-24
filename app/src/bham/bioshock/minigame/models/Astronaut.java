package bham.bioshock.minigame.models;

import static java.util.stream.Collectors.toList;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import bham.bioshock.client.Assets;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.CollisionBoundary;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.physics.Step;
import bham.bioshock.minigame.utils.RotatableText;
import bham.bioshock.minigame.worlds.World;

public class Astronaut extends Entity {

  private static final long serialVersionUID = 3467047831018591965L;
  
  private static AstronautTextures[] textures;
  private static TextureRegion[] hearts = new TextureRegion[5];
  
  private AstronautTextures texture; 
  private Sprite health;
  float animationTime;
  private PlayerTexture dir = PlayerTexture.FRONT;
  private boolean haveGun = false;
  private CollisionBoundary legs;
  private Move move = new Move();
  private Position respawn;
  private RotatableText name;
  
  private boolean dieFront = true;
  private int colour;
  private float dieTime;
  private Direction shotDirection;
  private transient Optional<Entity> currentPlatform = Optional.empty();

  public Astronaut(World w, float x, float y, UUID id, int colour) {
    super(w, x, y, EntityType.ASTRONAUT);
    width = 150;
    height = 150;
    animationTime = 0;
    fromGround = -20;
    update(0);
    collisionWidth = 65;
    collisionHeight = 150 + fromGround;
    this.colour = colour;
    this.id = id;
  }

  public Astronaut(World w, Position p, UUID id, int colour) {
    this(w, p.x, p.y, id, colour);
  }

  public void setName(String name) {
    this.name = new RotatableText(name);
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
    if(is(State.REMOVING)) return;
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
    
    if (is(State.REMOVING)) {
      collisionBoundary.update(respawn, delta);
      collisionBoundary.update(pos, getRotation() - (dieTime / 0.71f)*90);
      dieTime += delta;
    }
    legs.update(pos, getRotation());
    animationTime += delta;

    checkIfOnGround();
  }

  private void checkIfOnGround() {
    if((world.fromGroundTo(legs.getX(),legs.getY())) <= 15) {
      removePlatform();
    }
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
  public void draw(SpriteBatch batch) {
    if (is(State.REMOVING)) {
      if ((dieFront && texture.dyingFront.isAnimationFinished(dieTime))
          || texture.dyingBack.isAnimationFinished(dieTime)) {
        
        stepsGenerator.updateFromServer(new SpeedVector(), respawn);
        setState(State.LOADED);
        
      }
      
    } else {
      drawHealth(batch);
    }
    super.draw(batch);
  }

  @Override
  public void afterDraw(SpriteBatch batch) {
    drawName(batch);
  }
  
  private void drawHealth(SpriteBatch batch) {
    if(!objective.isPresent()) return;
    Objective o = objective.get();
    int value = o.getHealth(getId());
    
    PlanetPosition pp = world.convert(pos);
    pp.fromCenter += height;
    Position lifePos = world.convert(pp);
    health.setRegion(hearts[Math.min(4, Math.max(0, 4-value))]);
    health.setPosition(lifePos.x - (health.getWidth() / 2), lifePos.y);
    health.setRotation((float) getRotation());
    health.draw(batch);
  }
  
  private void drawName(SpriteBatch batch) {
    PlanetPosition pp = world.convert(pos);
    pp.fromCenter += height + 40;
    Position namePosition = world.convert(pp);
    name.update(namePosition, getRotation());
    name.draw(batch);
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
    texture = textures[colour];
    super.load();    
    legs = new CollisionBoundary(collisionWidth + 10, collisionHeight / 10);
    legs.update(pos, getRotation());
    
    health = new Sprite(hearts[0]);
    float healthWidth = 50;
    health.setSize(healthWidth, (health.getHeight()/health.getWidth()) * healthWidth);
    health.setOrigin(health.getWidth()/2, 0);
  }

  /**
   * Player textures
   **/
  public TextureRegion getTexture() {
    // If removing show dying animation
    if (is(State.REMOVING)) {
      if(dieFront) {
        return texture.dyingFront.getKeyFrame(dieTime, false);        
      }
      return texture.dyingBack.getKeyFrame(dieTime, false);  
    }
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
      return texture.frontGunTexture;
    } else if (dir.equals(PlayerTexture.FRONT)) {
      return texture.frontTexture;
    } else if (withGun) {
      return texture.walkGunAnimation.getKeyFrame(animationTime, true);
    }

    return texture.walkAnimation.getKeyFrame(animationTime, true);
  }


  private static TextureRegion[][] splittedTexture(AssetManager manager, String path, int fnum) {
    Texture t = manager.get(path, Texture.class);
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
  public boolean canCollideWith(Entity e) {
    switch (e.type) {
      case ASTRONAUT:
      case BULLET:
      case GUN:
      case PLATFORM:
      case FLAG:
      case GOAL:
      case HEART:
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
        if(objective.isPresent()) {
          objective.get().gotShot(this, ((Bullet) e).getShooter());            
        }
        e.setState(State.REMOVING);
        break;
      case FLAG:
        Flag flag = (Flag) e;
        if(objective.isPresent() && !flag.haveOwner()) {
          objective.get().captured(this);
        }
        break;
      case GOAL:
        if(objective.isPresent()) {
          objective.get().captured(this);
        }
        break;
      case PLATFORM:
        // Check collision with legs
        if (legs.collideWith(e.collisionBoundary, null)) {
          // Standing on the platform
          setOnPlatform((Platform) e);
        }
        break;
      case HEART:
        if (objective.isPresent()) {
          Heart heart = (Heart) e;
          objective.get().pickupHeart(this, heart.id);
          heart.remove();
        }
        break;
      default:
        break;
    }
  }

  @Override
  public boolean handleCollisionMove(Step step, MinimumTranslationVector v, Entity e) {
    switch (e.type) {
      case BULLET:
        if (e.is(State.REMOVING)) return false;
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

  public void updateFromServer(SpeedVector speed, Position pos, Boolean haveGun) {
    this.haveGun = haveGun;
    stepsGenerator.updateFromServer(speed, pos);
  }
  
  public void updateMove(Move move) {
    this.move = move;
    this.moveChange();
  }

  /**
   * Start dying animation and set state to REMOVING
   * 
   * @param pos new respawn position
   */
  public void killAndRespawn(Position pos) {
    if(is(State.REMOVING)) return;
    setState(State.REMOVING);
    dieTime = 0;
    dieFront = true;
    
    // Get direction of the shot
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
    this.respawn = pos;
  }
  
  public static void loadTextures(AssetManager manager) {
    String[] colours = new String[] { "orange", "red", "green", "blue" };

    for(int i=0; i<colours.length; i++) {
      manager.load(Assets.astroBase + colours[i] + Assets.astroWalk, Texture.class);
      manager.load(Assets.astroBase + colours[i] + Assets.astroGun, Texture.class);
      manager.load(Assets.astroBase + colours[i] + Assets.astroFFall, Texture.class);
      manager.load(Assets.astroBase + colours[i] + Assets.astroFall, Texture.class);
    }
    manager.load("app/assets/minigame/hearts.png", Texture.class);
  }
  
  public static void createTextures(AssetManager manager) {
    String[] colours = new String[] { "orange", "red", "green", "blue" };
    textures = new AstronautTextures[colours.length];
    
    for(int i=0; i<colours.length; i++) {
      AstronautTextures t = new AstronautTextures();
      TextureRegion[][] walkSheet = splittedTexture(manager, Assets.astroBase + colours[i] + Assets.astroWalk, 11);
      TextureRegion[][] walkGunSheet = splittedTexture(manager, Assets.astroBase + colours[i] + Assets.astroGun, 11);
      TextureRegion[][] dieFront = splittedTexture(manager, Assets.astroBase + colours[i] + Assets.astroFFall, 10);
      TextureRegion[][] dieBack = splittedTexture(manager, Assets.astroBase + colours[i] + Assets.astroFall, 12);
      
      t.frontTexture = walkSheet[0][0];
      t.frontGunTexture = walkGunSheet[0][0];

      t.walkAnimation = textureToAnimation(walkSheet, 11, 1, 0.1f);
      t.walkGunAnimation = textureToAnimation(walkGunSheet, 11, 1, 0.1f);
      t.dyingFront = textureToAnimation(dieFront, 10, 0, 0.05f);
      t.dyingBack = textureToAnimation(dieBack, 12, 0, 0.05f);
      textures[i] = t;
    }
    
    TextureRegion[][] h = splittedTexture(manager, "app/assets/minigame/hearts.png", 5);
    for(int i=0; i<h[0].length; i++) {
      hearts[i] = h[0][i];
    }
 
  }
  
  public static class Move implements Serializable {

    private static final long serialVersionUID = 3668803304780843571L;
    public boolean jumping = false;
    public boolean movingLeft = false;
    public boolean movingRight = false;
    
    public Move copy() {
      Move m = new Move();
      m.jumping = this.jumping;
      m.movingLeft = this.movingLeft;
      m.movingRight = this.movingRight;
      return m;
    }
    
    public String toString() {
      return "j: " + jumping + ", l: " + movingLeft + ", r: "+ movingRight;
    }
  }
  
  private static class AstronautTextures {
    Animation<TextureRegion> walkAnimation;
    Animation<TextureRegion> walkGunAnimation;
    Animation<TextureRegion> dyingFront;
    Animation<TextureRegion> dyingBack;
    TextureRegion frontTexture;
    TextureRegion frontGunTexture;
  }


  private void setOnPlatform(Platform platform) {
    currentPlatform = Optional.of(platform);
  }
  private void removePlatform() {
    currentPlatform = Optional.empty();
  }
  public Optional<Entity> getOnPlatform() {
    return currentPlatform;
  }

  public String toString() {
    return name.getText();
  }
}
