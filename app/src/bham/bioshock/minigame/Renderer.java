package bham.bioshock.minigame;

import bham.bioshock.client.scenes.MinigameHud;
import bham.bioshock.client.screens.StatsContainer;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;

import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.physics.Gravity;
import bham.bioshock.minigame.worlds.World;
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

import java.util.ArrayList;

public class Renderer {

  private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
  private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
  ShapeRenderer renderer;
  Vector3 lerpTarget = new Vector3();
  private Player mainPlayer;
  private Clock clock;
  private ArrayList<Entity> entities;
  private OrthographicCamera cam;
  private Sprite background;
  private Stage stage;
  private SpriteBatch batch;
  private SpriteBatch backgroundBatch;
  private Viewport viewport;
  private double camRotation;

  private Store store;


  private MinigameStore minigameStore;
  private Gravity gravity;
  private Router router;

  private MinigameHud hud;
  private final InputMultiplexer inputMultiplexer;

  public Renderer(Store store, Router router) {
    this.store = store;
    this.minigameStore = store.getMinigameStore();
    this.router = router;
    mainPlayer = minigameStore.getMainPlayer();

    renderer = new ShapeRenderer();
    clock = new Clock();
    entities = new ArrayList<Entity>();
    entities.addAll(minigameStore.getPlayers());
    entities.addAll(minigameStore.getRockets());
    gravity = new Gravity(minigameStore.getWorld());

    cam = new OrthographicCamera();

    batch = new SpriteBatch();
    backgroundBatch = new SpriteBatch();
    camRotation = 0;

    cam.update();

    setupUI();
    loadSprites();

    // Setup the input processing
    this.inputMultiplexer = new InputMultiplexer();
    this.inputMultiplexer.addProcessor(hud.getStage());
    this.inputMultiplexer.addProcessor(stage);


//    startClock();
  }

  private void setupUI() {
    Skin skin = new Skin(Gdx.files.internal("app/assets/skins/neon/skin/neon-ui.json"));
    hud = new MinigameHud(batch, skin, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, store, router);
  }


  public void loadSprites() {
    viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, cam);
    Player.loadTextures();
    Rocket.loadTextures();
    stage = new Stage(viewport);

    background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));

    mainPlayer.load();

    for (Entity e : entities) {
      e.load();
    }

  }
  
//  public void startClock() {
//    clock.every(0.01f, clock.new TimeListener() {
//      @Override
//      public void handle(TimeUpdateEvent event) {
//        router.call(Route.MINIGAME_MOVE);
//      }
//    });
//  }
 
  public void render(float delta) {
//    clock.update(delta);
  
    batch.setProjectionMatrix(cam.combined);
    renderer.setProjectionMatrix(cam.combined);

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

    //stage.addActor(statsContainer);

    drawPlanet();
    batch.begin();
    drawEntities();
    //drawMainPlayer();

    batch.end();

    // Draw the ui
    this.batch.setProjectionMatrix(hud.stage.getCamera().combined);
    hud.getStage().act(Gdx.graphics.getDeltaTime());
    hud.updateHud();
    hud.getStage().draw();

  }

  public void drawPlanet() {
    renderer.begin(ShapeType.Filled);
    renderer.setColor(Color.SALMON);
    renderer.circle(0, 0, (float) minigameStore.getPlanetRadius());
    renderer.end();
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
    
    if(moveMade) {
      router.call(Route.MINIGAME_MOVE);
    }
  }


  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }


}
