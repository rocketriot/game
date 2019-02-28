package bham.bioshock.minigame;

import bham.bioshock.client.scenes.MinigameHud;


import java.util.ArrayList;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.minigame.worlds.World.PlanetPosition;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Renderer {

  private static boolean DEBUG_MODE = false;
  private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
  private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
  private Player mainPlayer;
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
  private World world;
  private Store store;
  private MinigameStore minigameStore;

  private Router router;
  private boolean firstRender = true;

  private MinigameHud hud;
  private final InputMultiplexer inputMultiplexer;

  public Renderer(Store store, Router router) {
    this.store = store;
    this.minigameStore = store.getMinigameStore();
    this.router = router;
    mainPlayer = minigameStore.getMainPlayer();

    shapeRenderer = new ShapeRenderer();
    entities = new ArrayList<Entity>();
    entities.addAll(minigameStore.getPlayers());
    entities.addAll(minigameStore.getRockets());
    entities.addAll(minigameStore.getGuns());
    world = minigameStore.getWorld();
 
    cam = new OrthographicCamera();
    cam.position.set(mainPlayer.getX(), mainPlayer.getY(), 0);
    camRotation = 0;
    cam.update();
    
    batch = new SpriteBatch();
    backgroundBatch = new SpriteBatch();

    setupUI();
    loadSprites();

    // Setup the input processing
    this.inputMultiplexer = new InputMultiplexer();
    this.inputMultiplexer.addProcessor(hud.getStage());
    this.inputMultiplexer.addProcessor(stage);
  }

  private void setupUI() {
    Skin skin = new Skin(Gdx.files.internal("app/assets/skins/neon/skin/neon-ui.json"));
    hud = new MinigameHud(batch, skin, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, store, router);
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
  }

  public void render(float delta) {
    batch.setProjectionMatrix(cam.combined);
    shapeRenderer.setProjectionMatrix(cam.combined);
    if(!firstRender) {
      handleCollisions();
    } 

    cam.position.lerp(lerpTarget.set(mainPlayer.getX(), mainPlayer.getY(), 0), 3f * delta);

    double rotation = -world.getAngleTo(cam.position.x, cam.position.y);
    cam.rotate((float) (camRotation - rotation));
    camRotation = rotation;
    cam.update();

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    backgroundBatch.begin();
    backgroundBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    backgroundBatch.end();

    //stage.addActor(statsContainer);

    drawPlanet();

    if (DEBUG_MODE) {
      drawDebug();
    }

    batch.begin();
    drawEntities();
    //drawMainPlayer();

    batch.end();

    // Draw the ui
    this.batch.setProjectionMatrix(hud.stage.getCamera().combined);
    hud.getStage().act(Gdx.graphics.getDeltaTime());
    hud.updateHud();
    hud.getStage().draw();

    updatePosition();
    firstRender = false;
  }


  public void drawPlanet() {
    shapeRenderer.begin(ShapeType.Filled);
    shapeRenderer.setColor(Color.SALMON);
    shapeRenderer.circle(0, 0, (float) minigameStore.getPlanetRadius());
    
    shapeRenderer.end();
  }

  public void drawDebug() {
    for (Entity e : entities) {
      e.drawDebug(shapeRenderer);
    }
  }

  public void handleCollisions() {
    // Check collisions between any two entities
    for (Entity e1 : entities) {
      for (Entity e2 : entities) {
        if (!e1.equals(e2) && e1.checkCollision(e2)) {
          e1.handleCollision(e2);
        }
       }
    }
  }

  public void createBullet() {
    Player main = minigameStore.getMainPlayer();
    PlanetPosition pp = world.convert(main.getPosition());
    pp.fromCenter += main.getSize() / 2;
    if(main.getDirection().equals(PlayerTexture.LEFT)) {
      pp.angle -= 2;
    } else if(main.getDirection().equals(PlayerTexture.RIGHT)) {
      pp.angle += 2;
    }
    Position bulletPos = world.convert(pp);
    
    Bullet b = new Bullet(minigameStore.getWorld(), bulletPos.x, bulletPos.y);
    // First synchronise the bullet with the player
    b.setSpeedVector((SpeedVector) main.getSpeedVector().clone());
    // Apply bullet speed
    b.setSpeed((float) main.getSpeedVector().getSpeedAngle(), Bullet.launchSpeed);    
    router.call(Route.MINIGAME_BULLET_SEND, b);
    addBullet(b);
  }
  
  public void addBullet(Bullet b) {
    b.load();
    entities.add(b);
  }

  public void drawEntities() {
    entities.removeIf(e -> e.isRemoved());
    for (Entity e : entities) {
      Sprite sprite = e.getSprite();
      sprite.setRegion(e.getTexture());
      sprite.setPosition(e.getX() - (sprite.getWidth() / 2), e.getY());
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

    if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && mainPlayer.haveGun()) {
      createBullet();
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


