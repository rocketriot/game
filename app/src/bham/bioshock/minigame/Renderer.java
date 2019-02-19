package bham.bioshock.minigame;


import java.util.ArrayList;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.Clock.TimeUpdateEvent;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.physics.Gravity;
import bham.bioshock.minigame.physics.SpeedVector;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Renderer {

  private static boolean DEBUG_MODE = false;
  private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
  private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
  private Player mainPlayer;
  private Clock clock;
  private ArrayList<Entity> entities;

  ShapeRenderer shapeRenderer;
  private OrthographicCamera cam;
  Vector3 lerpTarget = new Vector3();
  private Sprite background;
  private Stage stage;
  private SpriteBatch batch;
  private SpriteBatch backgroundBatch;
  private Viewport viewport;
  private double camRotation;
  private MinigameStore store;
  private Gravity gravity;
  private Router router;
  private boolean shooting;

  public Renderer(MinigameStore store, Router router) {
    this.store = store;
    this.router = router;
    mainPlayer = store.getMainPlayer();
    shapeRenderer = new ShapeRenderer();
    entities = new ArrayList<Entity>();
    entities.addAll(store.getPlayers());
    entities.addAll(store.getRockets());
    entities.addAll(store.getGuns());
    gravity = new Gravity(store.getWorld());
    clock = new Clock();
    shooting = false;

    cam = new OrthographicCamera();
    batch = new SpriteBatch();
    backgroundBatch = new SpriteBatch();
    camRotation = 0;

    cam.update();

    loadSprites();
    startClock();
  }


  public void loadSprites() {
    viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, cam);
    Player.loadTextures();
    Rocket.loadTextures();
    Gun.loadTextures();
    Bullet.loadTextures();
    stage = new Stage(viewport);
    background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));

    for (Entity e : entities) {
      e.load();
    }

    Gdx.input.setInputProcessor(new InputAdapter() {
      @Override
      public boolean keyDown(int keyCode) {
        if (Keys.SPACE == keyCode && !shooting) {
          createBullet();
          shooting = true;
        }
        return true;
      }

      @Override
      public boolean keyUp(int keyCode) {
        if (Keys.SPACE == keyCode) {
          shooting = false;
        }
        return true;
      }
    });
  }

  public void startClock() {
    clock.at(15, clock.new TimeListener() {
      @Override
      public void handle(TimeUpdateEvent event) {
        // router.call(Route.SERVER_MINIGAME_END);
      }
    });
  }

  public void render(float delta) {
    clock.update(delta);

    batch.setProjectionMatrix(cam.combined);
    shapeRenderer.setProjectionMatrix(cam.combined);

    if (DEBUG_MODE) {
      drawCollisionBorders();
    }

    handleCollisions();
    updatePosition();
    cam.position.lerp(lerpTarget.set(mainPlayer.getX(), mainPlayer.getY(), 0), 3f * delta);

    double rotation = -gravity.getAngleTo(cam.position.x, cam.position.y);
    cam.rotate((float) (camRotation - rotation));
    camRotation = rotation;
    cam.update();

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    backgroundBatch.begin();
    backgroundBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    backgroundBatch.end();

    drawPlanet();
    batch.begin();
    drawEntities();
    batch.end();
  }


  public void drawPlanet() {
    shapeRenderer.begin(ShapeType.Filled);
    shapeRenderer.setColor(Color.SALMON);

    shapeRenderer.circle(0, 0, (float) store.getPlanetRadius());
    // bounding circle
    new Circle(0, 0, (float) store.getPlanetRadius() - 50);

    shapeRenderer.end();
  }

  public void drawCollisionBorders() {
    for (Entity e : entities) {
      Rectangle border = e.getRectangle();
      shapeRenderer.begin(ShapeType.Filled);
      shapeRenderer.setColor(Color.BLACK);
      shapeRenderer.rect(border.getX(), border.getY(), border.getWidth(), border.getHeight());
      shapeRenderer.end();
    }
  }

  public void handleCollisions() {
    // Check collisions between any two entities
    for (Entity e1 : entities) {
      for (Entity e2 : entities) {
        if (!e1.equals(e2)) {
          e1.checkCollision(e2);
        }
      }
    }
  }
  
  public void createBullet() {
    Player main = store.getMainPlayer();
    Bullet b = new Bullet(store.getWorld(), main.getX(), main.getY() + main.getSize()*3/4);
    
    // First synchronise the bullet with the player
    b.setSpeedVector((SpeedVector) main.getSpeedVector().clone());
    b.setSpeed((float) main.getSpeedVector().getSpeedAngle(), 2000);
    b.load();
    entities.add(b);
  }

  public void drawEntities() {
    for (Entity e : entities) {
      Sprite sprite = e.getSprite();
      sprite.setRegion(e.getTexture());
      sprite.setPosition(e.getX(), e.getY());
      sprite.setRotation((float) e.getRotation());
      sprite.draw(batch);
      e.update(Gdx.graphics.getDeltaTime());
    }
  }


  public void updatePosition() {
    float dt = Gdx.graphics.getDeltaTime();
    boolean moveMade = false;

    if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
      moveMade = true;
      mainPlayer.moveLeft(dt);
    }

    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
      moveMade = true;
      mainPlayer.moveRight(dt);
    }

    if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
      moveMade = true;
      mainPlayer.jump(dt);
    }

    if (moveMade) {
      // Send a move to the server
      router.call(Route.MINIGAME_MOVE);
    }

  }


  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

}


