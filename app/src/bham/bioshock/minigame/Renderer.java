package bham.bioshock.minigame;

import bham.bioshock.Config;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.scenes.minigame.MinigameHud;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.CollisionHandler;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Collection;

/** Main minigame renderer */
public class Renderer {

  private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
  private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
  /** Main player */
  private Astronaut mainPlayer;
  /** Rendering components */
  private ShapeRenderer shapeRenderer;
  private Sprite background;
  private Stage stage;
  private SpriteBatch batch;
  private SpriteBatch backgroundBatch;
  private SpriteBatch textBatch;
  private Viewport viewport;
  /** Camera properties */
  private OrthographicCamera cam;
  private Vector3 lerpTarget = new Vector3();
  private double camRotation;
  /** Stores and router */
  private Store store;

  private Router router;
  private MinigameStore minigameStore;

  private MinigameHud hud;
  private World world;
  private AssetContainer assets;
  private float time;

  /**
   * Create a renderer, store minigame store
   *
   * @param store
   * @param router
   * @param manager
   */
  public Renderer(Store store, Router router, AssetContainer assets) {
    this.store = store;
    this.minigameStore = store.getMinigameStore();
    this.router = router;
    this.assets = assets;
  }

  /** Initialise rendering components */
  public void show() {
    mainPlayer = minigameStore.getMainPlayer();
    shapeRenderer = new ShapeRenderer();
    world = minigameStore.getWorld();

    cam = new OrthographicCamera();
    camRotation = 0;

    batch = new SpriteBatch();
    textBatch = new SpriteBatch();
    backgroundBatch = new SpriteBatch();

    CollisionHandler collisionHandler = new CollisionHandler(minigameStore);
    minigameStore.setCollisionHandler(collisionHandler);

    hud = new MinigameHud(batch, assets, store, router);

    loadSprites();

    // Setup the input processing
    InputMultiplexer multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(stage);
    multiplexer.addProcessor(
        new InputListener(minigameStore, router, minigameStore.getCollisionHandler(), hud));
    Gdx.input.setInputProcessor(multiplexer);
  }

  public void loadSprites() {
    CollisionHandler collisionHandler = minigameStore.getCollisionHandler();
    viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, cam);
    stage = new Stage(viewport);

    Astronaut.createTextures(assets);
    Rocket.createTextures(assets);
    Gun.createTextures(assets);
    Bullet.createTextures(assets);
    Flag.createTextures(assets);
    Heart.createTextures(assets);
    Goal.createTextures(assets);
    World.createTextures(assets, world.getTextureId());
    Platform.createTextures(assets, world.getTextureId());

    background = new Sprite(assets.get(Assets.gameBackground, Texture.class));

    Objective objective = minigameStore.getObjective();

    for (Entity e : getEntities()) {
      e.load();
      e.setCollisionHandler(collisionHandler);
      if (objective != null) {
        e.setObjective(minigameStore.getObjective());
      }
    }
  }

  /**
   * Get all entities, both dynamic and static
   *
   * @return list of all entities to render
   */
  public Collection<Entity> getEntities() {
    Collection<Entity> entities = new ArrayList<>(minigameStore.countEntities());
    entities.addAll(minigameStore.getEntities());
    entities.addAll(minigameStore.getStaticEntities());
    return entities;
  }

  /**
   * Main render function
   *
   * @param delta
   */
  public void render(float delta) {
    // If client disconnected show reconnecting screen
    if (store.isReconnecting()) {
      router.call(Route.LOADING, "Reconnecting...");
      return;
    }

    time += delta;
    batch.setProjectionMatrix(cam.combined);
    textBatch.setProjectionMatrix(cam.combined);
    shapeRenderer.setProjectionMatrix(cam.combined);

    // Update the camera rotation
    cam.position.lerp(lerpTarget.set(mainPlayer.getX(), mainPlayer.getY(), 0), 3f * delta);
    double rotation = -world.getAngleTo(cam.position.x, cam.position.y);
    cam.rotate((float) (camRotation - rotation));
    camRotation = rotation;
    cam.update();

    // Clear the screen
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // Get all entities
    Collection<Entity> entities = getEntities();

    // Draw background
    drawBackground();

    if (Config.DEBUG_RENDERER) {
      entities.forEach(e -> e.drawDebug(shapeRenderer));
    }

    batch.begin();
    world.draw(batch);
    entities.forEach(e -> e.draw(batch));
    batch.end();
    entities.forEach(e -> e.afterDraw(textBatch));
    entities.forEach(e -> e.update(delta));
    world.afterDraw(batch);

    // Draw the ui
    batch.setProjectionMatrix(hud.getStage().getCamera().combined);
    hud.getStage().act(delta);
    hud.update();
    hud.getStage().draw();
    minigameStore.getEntities().removeIf(e -> e.isRemoved());
    minigameStore.getStaticEntities().removeIf(e -> e.isRemoved());

    // Send to server updated player position every second
    if (time > 1f) {
      time -= 1f;
      router.call(Route.MINIGAME_STEP);
    }
  }

  /** Draw background */
  public void drawBackground() {
    backgroundBatch.begin();
    backgroundBatch.disableBlending();
    backgroundBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    backgroundBatch.end();
  }

  /**
   * Resize the viewport
   *
   * @param width
   * @param height
   */
  public void resize(int width, int height) {
    if (stage != null) {
      stage.getViewport().update(width, height, true);
    }
  }

  /** Dispose all rendering components */
  public void dispose() {
    if (batch != null) {
      batch.dispose();
    }
    if (backgroundBatch != null) {
      backgroundBatch.dispose();
    }
    textBatch.dispose();
    hud.dispose();
  }
}
