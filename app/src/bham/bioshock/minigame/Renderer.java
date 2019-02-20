package bham.bioshock.minigame;

import bham.bioshock.client.scenes.MinigameHud;
import bham.bioshock.client.screens.StatsContainer;


import java.util.ArrayList;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.Map;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.Clock.TimeUpdateEvent;
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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Renderer {
    private Player mainPlayer;
    private ArrayList<Entity> entities;
    private ArrayList<StaticEntity> staticEntities;

    ShapeRenderer shapeRenderer;
    private OrthographicCamera cam;
    Vector3 lerpTarget = new Vector3();
    private Sprite background;
    private Stage stage;
    private SpriteBatch batch;
    private SpriteBatch backgroundBatch;
    private Viewport viewport;
    private double camRotation;
    private Map map;
    private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
    private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
    private Circle mainPlanet;
    private MinigameStore store;
    private Router router;
    private static boolean DEBUG_MODE = false;
    private Clock clock;
    private MinigameStore minigameStore;
    private boolean shooting;
    private boolean firstRender = true;
    private MinigameHud hud;
    //private final InputMultiplexer inputMultiplexer;



    public Renderer(MinigameStore store, Router router, Map map) {
        this.store = store;
        this.router = router;
        mainPlayer = store.getMainPlayer();
        shapeRenderer = new ShapeRenderer();
        entities = new ArrayList<Entity>();
        staticEntities = new ArrayList<StaticEntity>();
        entities.addAll(store.getPlayers());
        entities.addAll(store.getRockets());
        staticEntities.addAll(map.getPlatforms());

        cam = new OrthographicCamera();
        map = new Map(store);
        batch = new SpriteBatch();
        backgroundBatch = new SpriteBatch();
        camRotation = 0;

        cam.update();

        loadSprites();
        //startClock();
    }



  public Renderer(Store store, Router router) {
    this.minigameStore = store.getMinigameStore();
    this.router = router;
    mainPlayer = minigameStore.getMainPlayer();

    shapeRenderer = new ShapeRenderer();
    entities = new ArrayList<Entity>();
    entities.addAll(minigameStore.getPlayers());
    entities.addAll(minigameStore.getRockets());
    entities.addAll(minigameStore.getGuns());
    shooting = false;
 
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

    mainPlayer.load();

    for (Entity e : entities) {
      e.load();
    }

    Gdx.input.setInputProcessor(new InputAdapter() {
      @Override
      public boolean keyDown(int keyCode) {
        if (Keys.SPACE == keyCode && !shooting && mainPlayer.haveGun()) {
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
      for (StaticEntity e : staticEntities) {
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




    public void createBullet() {
        Player main = minigameStore.getMainPlayer();
        PlanetPosition pp = world.convert(main.getPosition());
        pp.fromCenter += main.getSize() / 2;
        Position bulletPos = world.convert(pp);

        Bullet b = new Bullet(minigameStore.getWorld(), bulletPos.x, bulletPos.y);
        // First synchronise the bullet with the player
        b.setSpeedVector((SpeedVector) main.getSpeedVector().clone());
        b.setSpeed((float) main.getSpeedVector().getSpeedAngle(), Bullet.launchSpeed);
        router.call(Route.MINIGAME_BULLET_SEND, b);
        addBullet(b);
    }

    public void addBullet(Bullet b) {
        b.load();
        entities.add(b);
    }


  public void drawEntities(){
        entities.removeIf(e->e.isRemoved());
        for(Entity e:entities){
        Sprite sprite=e.getSprite();
        sprite.setRegion(e.getTexture());
        sprite.setPosition(e.getX()-(sprite.getWidth()/2),e.getY());
        sprite.setRotation((float)e.getRotation());
        sprite.draw(batch);
        e.update(Gdx.graphics.getDeltaTime());
        }
        for(StaticEntity e:staticEntities){
        Sprite sprite=e.getSprite();
        sprite.setRegion(e.getTexture());
        sprite.setPosition(e.getX(),e.getY());
        sprite.draw(batch);

        }

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

                for (StaticEntity e3 :staticEntities){
                    e3.checkCollision(e1);
                }
            }
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

    public void shoot(){
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            float xBullet;
            if(mainPlayer.getX() >0)
                xBullet = mainPlayer.getX() + mainPlayer.getSprite().getHeight()/2;
            else
                xBullet = mainPlayer.getX() - mainPlayer.getSprite().getHeight()/2;

            Bullet b = new Bullet (store.getWorld(),xBullet,mainPlayer.getY());
            b.load();
            entities.add(b);
            float dt = Gdx.graphics.getDeltaTime();
            b.update(dt);
        }

    }

}


