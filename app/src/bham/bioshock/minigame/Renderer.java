package bham.bioshock.minigame;

import java.util.ArrayList;

import bham.bioshock.client.Assets;
import bham.bioshock.client.Router;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.worlds.World;
import java.util.Collection;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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
import bham.bioshock.client.scenes.minigame.MinigameHud;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.physics.CollisionHandler;

public class Renderer {
  private Astronaut mainPlayer;

  ShapeRenderer shapeRenderer;
  private OrthographicCamera cam;
  Vector3 lerpTarget = new Vector3();
  private Sprite background;
  private Stage stage;
  private SpriteBatch batch;
  private SpriteBatch backgroundBatch;
  private SpriteBatch textBatch;
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

  private Texture worldTexture;
  
  public Renderer(Store store, Router router) {
    this.store = store;
    this.minigameStore = store.getMinigameStore();
    this.router = router;

    mainPlayer = minigameStore.getMainPlayer();

    shapeRenderer = new ShapeRenderer();

    world = minigameStore.getWorld();
    worldTexture = world.getTexture();

    cam = new OrthographicCamera();
    camRotation = 0;
    cam.update();

    batch = new SpriteBatch();
    textBatch = new SpriteBatch();
    backgroundBatch = new SpriteBatch();

    CollisionHandler collisionHandler = new CollisionHandler(minigameStore);

    setupUI();
    loadSprites(collisionHandler);

    // Setup the input processing
    InputMultiplexer multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(stage);
    multiplexer.addProcessor(new InputListener(minigameStore, router, collisionHandler, hud));
    Gdx.input.setInputProcessor(multiplexer);
  }

  private void setupUI() {
    Skin skin = new Skin(Gdx.files.internal(Assets.skin));
    hud = new MinigameHud(batch, skin, store, router);
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

    Objective objective = minigameStore.getObjective();
    
    for (Entity e : getEntities()) {
      e.load();
      e.setCollisionHandler(collisionHandler);
      if(objective != null) {
        e.setObjective(minigameStore.getObjective());        
      }
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
    textBatch.setProjectionMatrix(cam.combined);
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
    world.draw(batch);
    entities.forEach(e -> e.draw(batch));
    batch.end();
    entities.forEach(e -> e.afterDraw(textBatch));
    entities.forEach(e -> e.update(delta));
    world.afterDraw(batch);
    
    // Draw the ui
    this.batch.setProjectionMatrix(hud.getStage().getCamera().combined);
    hud.getStage().act(delta);
    hud.update();
    hud.getStage().draw();
    minigameStore.getEntities().removeIf(e -> e.isRemoved());
    minigameStore.getStaticEntities().removeIf(e -> e.isRemoved());
  }

  public void drawBackground() {
    backgroundBatch.begin();
    backgroundBatch.disableBlending();
    backgroundBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    backgroundBatch.end();
  }

  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }
}