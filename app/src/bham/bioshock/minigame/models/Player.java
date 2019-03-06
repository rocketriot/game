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

public class Player extends Entity {

  private static final int FRAMES = 11;
  private static Animation<TextureRegion> walkAnimation;
  private static Animation<TextureRegion> walkGunAnimation;
  private static TextureRegion frontTexture;
  private static TextureRegion frontGunTexture;
  float animationTime;
  private PlayerTexture dir = PlayerTexture.FRONT;
  private boolean haveGun = false;
  private float health = 0;
  private int kills = 0;
  private CollisionBoundary legs;
  public boolean isDead = false;

  public Player(World w, float x, float y) {
    super(w, x, y);
    width = 150;
    height = 150;
    animationTime = 0;
    fromGround = -20;
    update(0);
    collisionWidth = 65;
    collisionHeight = 150 + fromGround;
  }

  public Player(World w, Position p) {
    this(w, p.x, p.y);
  }

  public Player(World w) {
    this(w, 0f, 0f);
  }

  public void moveLeft() {
    stepsGenerator.moveLeft();
    dir = PlayerTexture.LEFT;
  }

  public void moveRight() {
    stepsGenerator.moveRight();
    dir = PlayerTexture.RIGHT;
  }

  public void jump() {
    SoundController.playSound("jump");
    stepsGenerator.jump();
  }
  
  public void moveStop() {
    stepsGenerator.moveStop();
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
    Stream<Step> futureSteps = stepsGenerator.getFutureSteps();
    
    shapeRenderer.begin(ShapeType.Filled);
    futureSteps.forEach(step -> {
      shapeRenderer.circle(step.position.x, step.position.y, 5);
    });
    shapeRenderer.end();      
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

  /** Collisions **/
  @Override
  public void handleCollision(Entity e) {
    boolean collided = false;
    
    if(e.isA(Bullet.class)) {
      // Collision check
      MinimumTranslationVector v = checkCollision(e);
      if(v == null) return;
      collided = true;
      
      collide(.2f, v);
      health -= 3;
      if(health<=0) {
        Bullet b = (Bullet)e;
        b.getShooter().addKills(1);
        this.isDead = true;
      }

    } else if(e.isA(Player.class) || e.isA(Rocket.class)) {
      // Collision check
      MinimumTranslationVector v = checkCollision(e);
      if(v == null) return;
      collided = true;
      
      collide(0.8f, v);
      
    } else if(e.isA(Gun.class)) {
      // Collision check
      MinimumTranslationVector v = checkCollision(e);
      if(v == null) return;
      collided = true;
      
      e.state = State.REMOVED;
      haveGun = true;
    } else if(e.isA(StaticEntity.class)) {
      
      // Standard collision check
      MinimumTranslationVector v = checkCollision(e);
      if(v!= null)
      {
        pos.x += v.normal.x * v.depth;
        pos.y += v.normal.y * v.depth;
        
        collide(0f, v);
        collided = true;
      }
      
      // Check collision with legs
      MinimumTranslationVector vlegs = new MinimumTranslationVector();
      if (legs.collideWith(e.collisionBoundary, vlegs)) {        
        // Standing on the platform
        super.onGround = true;
        collided = true;
      }
    }
    
    if(collided) {
      afterCollision();
    }
  }  

  public void setHealth(float newHealth){
    this.health = newHealth;
  }

  public float getHealth(){
    return this.health;
  }

  public void addKills(int addedKills){
    this.kills += addedKills;
  }

  public float getKills(){
    return this.kills;
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
