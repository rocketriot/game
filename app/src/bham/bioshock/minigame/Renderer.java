package bham.bioshock.minigame;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
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
import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.MinigameHud;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.objectives.KillThemAll;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.worlds.World;

public class Renderer {
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
  private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
  private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
  private Store store;
  private Router router;
  private static boolean DEBUG_MODE = true;
  private MinigameStore minigameStore;
  
  private boolean firstRender = true;
  private MinigameHud hud;
  private World world;
  private Clock clock;
  private Objective objective;


  public Renderer(Store store, Router router) {
    this.store = store;
    this.minigameStore = store.getMinigameStore();
    this.router = router;
    this.objective = new KillThemAll(minigameStore.getPlayers(), mainPlayer);
    mainPlayer = minigameStore.getMainPlayer();

    shapeRenderer = new ShapeRenderer();
    entities = new ArrayList<Entity>();

    world = minigameStore.getWorld();
    entities.addAll(world.getPlatforms());
    entities.addAll(minigameStore.getPlayers());
    entities.addAll(minigameStore.getRockets());
    entities.addAll(minigameStore.getGuns());

    world = minigameStore.getWorld();

    cam = new OrthographicCamera();
    //cam.position.set(mainPlayer.getX(), mainPlayer.getY(), 0);
    camRotation = 0;
    cam.update();

    batch = new SpriteBatch();
    backgroundBatch = new SpriteBatch();

    clock = new Clock();
    setupUI();
    loadSprites();

    // Setup the input processing
    InputMultiplexer multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(hud.getStage());
    multiplexer.addProcessor(stage);
    multiplexer.addProcessor(new InputListener(minigameStore, router));
    Gdx.input.setInputProcessor(multiplexer);
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
    clock.update(delta);
    batch.setProjectionMatrix(cam.combined);
    shapeRenderer.setProjectionMatrix(cam.combined);

    if (!firstRender) {
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

    drawPlanet();

    if (DEBUG_MODE) {
      drawDebug();
    }

    batch.begin();
    drawEntities(delta);

    batch.end();

    // Draw the ui
    this.batch.setProjectionMatrix(hud.stage.getCamera().combined);
    hud.getStage().act(delta);
    hud.updateHud();
    hud.getStage().draw();


    firstRender = false;
  }

  public void drawEntities(float delta) {
    for (Entity e : entities) {
      e.draw(batch, delta);
    }
    for (Entity e : minigameStore.getDynamicEntities()) {
      e.draw(batch, delta);
    }
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
    for (Entity e : entities) {
      e.resetColision();
    }

    // Check collisions between any two entities
    for (Entity e1 : entities) {
      for (Entity e2 : entities) {
        if (!e1.equals(e2)) {
          e1.handleCollision(e2);
        }
      }
    }
  }

  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

}


