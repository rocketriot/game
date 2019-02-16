package bham.bioshock.minigame;

import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.Clock.TimeUpdateEvent;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.physics.Gravity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
  private MinigameStore store;
  private Gravity gravity;

  public Renderer(MinigameStore store) {
    this.store = store;
    mainPlayer = store.getMainPlayer();
    renderer = new ShapeRenderer();
    clock = new Clock();
    entities = new ArrayList<Entity>();
    entities.addAll(store.getPlayers());
    entities.addAll(store.getRockets());
    gravity = new Gravity(store.getWorld());

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
    stage = new Stage(viewport);
    background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));

    for (Entity e : entities) {
      e.load();
    }
  }
  
  public void startClock() {
    clock.every(0.5f, clock.new TimeListener() {
      @Override
      public boolean handle(TimeUpdateEvent event) {
        System.out.println(event.time);
        return false;
      }
      
    });
  }
 
  public void render(float delta) {
    clock.update(delta);
  
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

    drawPlanet();
    batch.begin();
    drawEntities();
    batch.end();
  }

  public void drawPlanet() {
    renderer.begin(ShapeType.Filled);
    renderer.setColor(Color.SALMON);
    renderer.circle(0, 0, (float) store.getPlanetRadius());
    renderer.end();
  }

  public void drawEntities() {
    for (Entity e : entities) {
      Sprite sprite = e.getSprite();
      sprite.setRegion(mainPlayer.getTexture());
      sprite.setPosition(e.getX(), e.getY());
      sprite.setRotation((float) e.getRotation());
      sprite.draw(batch);
      e.update(Gdx.graphics.getDeltaTime());
    }
  }

  public void updatePosition() {
    float dt = Gdx.graphics.getDeltaTime();

    if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
      mainPlayer.moveLeft(dt);
    }
    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
      mainPlayer.moveRight(dt);
    }
    if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
      mainPlayer.jump(dt);
    }
  }


  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }
}
