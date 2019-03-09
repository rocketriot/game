package bham.bioshock.minigame;

import java.util.ArrayList;
import java.util.Collection;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.MinigameHud;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.CollisionHandler;
import bham.bioshock.minigame.worlds.World;

public class Renderer {
  private Astronaut mainPlayer;

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
  private static boolean DEBUG_MODE = false;
  private MinigameStore minigameStore;
  
  private MinigameHud hud;
  private World world;

  private Objective objective;
  private Texture worldTexture;
  
  public Renderer(Store store, Router router) {
    this.store = store;
    this.minigameStore = store.getMinigameStore();
    this.router = router;

    mainPlayer = minigameStore.getMainPlayer();

    shapeRenderer = new ShapeRenderer();

    world = minigameStore.getWorld();
    worldTexture = world.getTexture();

    this.objective = minigameStore.getObjective();
    this.objective.initialise();

    cam = new OrthographicCamera();
    camRotation = 0;
    cam.update();

    batch = new SpriteBatch();
    backgroundBatch = new SpriteBatch();
    
    CollisionHandler collisionHandler = new CollisionHandler(minigameStore);

    setupUI();
    loadSprites(collisionHandler);

    // Setup the input processing
    InputMultiplexer multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(hud.getStage());
    multiplexer.addProcessor(stage);
    multiplexer.addProcessor(new InputListener(minigameStore, router, collisionHandler));
    Gdx.input.setInputProcessor(multiplexer);
  }

  private void setupUI() {
    Skin skin = new Skin(Gdx.files.internal("app/assets/skins/neon/skin/neon-ui.json"));
    hud = new MinigameHud(batch, skin, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, store, router);
  }


  public void loadSprites(CollisionHandler collisionHandler) {
    viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, cam);
    Astronaut.loadTextures();
    Rocket.loadTextures();
    Gun.loadTextures();
    Bullet.loadTextures();
    Flag.loadTextures();
    stage = new Stage(viewport);

    background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));
    
    for (Entity e : getEntities()) {
      e.load();
      e.setCollisionHandler(collisionHandler);
      e.setObjective(objective);
    }
  }
  
  public Collection<Entity> getEntities() {
    Collection<Entity> entities = new ArrayList<>(minigameStore.countEntities());
    entities.addAll(minigameStore.getEntities());
    entities.addAll(minigameStore.getStaticEntities());
    return entities;
  }

  public void render(float delta) {
    batch.setProjectionMatrix(cam.combined);
    shapeRenderer.setProjectionMatrix(cam.combined);

    cam.position.lerp(lerpTarget.set(mainPlayer.getX(), mainPlayer.getY(), 0), 3f * delta);

    double rotation = -world.getAngleTo(cam.position.x, cam.position.y);
    cam.rotate((float) (camRotation - rotation));
    camRotation = rotation;
    cam.update();

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
    Collection<Entity> entities = getEntities();
    
    drawBackground();

    if (DEBUG_MODE) {
      entities.forEach(e -> e.drawDebug(shapeRenderer));
    }
    
    
    batch.begin();
    drawPlanet();
    entities.forEach(e -> e.draw(batch, delta));      
    batch.end();

    // Draw the ui
    this.batch.setProjectionMatrix(hud.stage.getCamera().combined);
    hud.getStage().act(delta);
    hud.updateHud();
    hud.getStage().draw();
    minigameStore.getEntities().removeIf(e -> e.isRemoved());
    minigameStore.getStaticEntities().removeIf(e -> e.isRemoved());
  }

  public void drawPlanet() {
    float radius = (float) world.getPlanetRadius()+530;
    batch.draw(worldTexture, -radius, -radius, radius*2, radius*2);
  }
  
  public void drawBackground() {
    backgroundBatch.begin();
    backgroundBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    backgroundBatch.end();
  }

  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }




}


