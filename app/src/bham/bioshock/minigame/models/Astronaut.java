package bham.bioshock.minigame.models;

import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.communication.Sendable;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.CollisionBoundary;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.physics.Step;
import bham.bioshock.minigame.utils.RotatableText;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

public class Astronaut extends Entity {

  private static final long serialVersionUID = -5131439342109870021L;
  private static Animation<TextureRegion> walkAnimation;
  private static Animation<TextureRegion> walkGunAnimation;
  private static TextureRegion frontTexture;
  private static TextureRegion frontGunTexture;
  private static TextureRegion[] hearts = new TextureRegion[5];
  private Sprite health;
  float animationTime;
  private PlayerTexture dir = PlayerTexture.FRONT;
  private boolean haveGun = false;
  private CollisionBoundary legs;
  private Move move = new Move();
  private Position respawn;
  private RotatableText name;
  
  static Animation<TextureRegion> dyingFront;
  static Animation<TextureRegion> dyingBack;
  private boolean dieFront = true;
  private float dieTime;
  private Direction shotDirection;
  private transient Optional<Entity> item = Optional.empty();
  private transient Optional<Entity> currentPlatform = Optional.empty();


  public Astronaut(World w, float x, float y, UUID id) {
    super(w, x, y, EntityType.ASTRONAUT);
    width = 150;
    height = 150;
    animationTime = 0;
    fromGround = -20;
    update(0);
    collisionWidth = 65;
    collisionHeight = 150 + fromGround;
    this.id = id;
  }

  public Astronaut(World w, Position p, UUID id) {
    this(w, p.x, p.y, id);
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
  
  public void setItem(Entity e) {
    this.item = Optional.of(e);
  }
  public void removeItem() {
    this.item = Optional.empty();
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
    //System.out.println((world.fromGroundTo(legs.getX(),legs.getY())));
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
      TextureRegion anim;
      if(dieFront) {
        anim = dyingFront.getKeyFrame(dieTime, false);        
      } else {
        anim = dyingBack.getKeyFrame(dieTime, false);
      }

      Sprite sprite = getSprite();
      sprite.setRegion(anim);
      sprite.setPosition(getX() - (sprite.getWidth() / 2), getY());
      sprite.setRotation((float) getRotation());
      sprite.draw(batch);
      
      if (dieFront && dyingFront.isAnimationFinished(dieTime)
          || dyingBack.isAnimationFinished(dieTime)) {
        setRotation(0);
        stepsGenerator.updateFromServer(new SpeedVector(), respawn);
        setState(State.LOADED);
      }
      
    } else {
      drawItem(batch);
      drawHealth(batch);
      super.draw(batch);
    }
  }

  @Override
  public void afterDraw(SpriteBatch batch) {
    drawName(batch);
  }
  
  private void drawItem(SpriteBatch batch) {
    if(!item.isPresent()) return;
    PlanetPosition pp = world.convert(pos);
    pp.fromCenter += height + 60;
    Position p = world.convert(pp);
    Entity e = item.get();
    e.getPos().x = p.x;
    e.getPos().y = p.y;
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
      case GOAL:
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
        if(objective.isPresent()) {
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
          //System.out.println("STANDING ON PLATFORM: "+e.toString());
          setOnPlatform((Platform) e);
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


  public static void loadTextures() {
    TextureRegion[][] walkSheet = splittedTexture("app/assets/minigame/astronaut.png", 11);
    TextureRegion[][] walkGunSheet = splittedTexture("app/assets/minigame/astronaut_gun.png", 11);
    TextureRegion[][] dieFront = splittedTexture("app/assets/minigame/die_front.png", 10);
    TextureRegion[][] dieBack = splittedTexture("app/assets/minigame/die_back.png", 12);
    TextureRegion[][] h = splittedTexture("app/assets/minigame/hearts.png", 5);
    
    for(int i=0; i<h[0].length; i++) {
      hearts[i] = h[0][i];
    }
    
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
    if(is(State.REMOVING)) return;
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
    
    item = Optional.empty();
    haveGun = false;
    setState(State.REMOVING);
    this.respawn = pos;
  }


  private void setOnPlatform(Platform platform) {
    //System.out.println(getId().toString() + "is on " + platform.toString() );
    currentPlatform = Optional.of(platform);
  }
  private void removePlatform() {
    //System.out.println(getId().toString() + "is on the ground");
    currentPlatform = Optional.empty();
  }
  public Optional<Entity> getOnPlatform() {
    return currentPlatform;
  }
  public boolean isOnPlatform(Platform platform) {
    // Check collision with legs
    if (legs.collideWith(platform.collisionBoundary, null)) {
      return true;
    }
    return false;
  }
  public boolean isOnGround() {
    return (getPlanetPos().fromCenter <= 2);
  }

  public String toString() {
    return name.getText();
  }
}
