package bham.bioshock.minigame;


import java.util.ArrayList;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.Map;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.*;
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
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    private Gravity gravity;
    private Router router;
    private static boolean DEBUG_MODE = false;


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
        gravity = new Gravity(store.getWorld());

        cam = new OrthographicCamera();
        map = new Map(store);
        batch = new SpriteBatch();
        backgroundBatch = new SpriteBatch();
        camRotation = 0;

        cam.update();

        loadSprites();
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

        for (StaticEntity e : staticEntities) {
            e.load();
        }
    }

    public void render(float delta) {
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);

        if (DEBUG_MODE) {
            drawCollisionBorders();
        }

        handleCollisions();
        updatePosition();
        shoot();

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
        mainPlanet = new Circle(0, 0, (float) store.getPlanetRadius() - 50);

        shapeRenderer.end();
    }

    public void drawCollisionBorders() {
        for (Entity e : entities) {
            drawBorder(e.getRectangle());
        }
    }


    public void drawBorder(Rectangle border) {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(border.getX(), border.getY(), border.getWidth(), border.getHeight());
        shapeRenderer.end();
    }


    public void drawBullet(float x, float y) {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(x, y, 10);
        shapeRenderer.end();
    }


    public void handleCollisions() {
        // Check collisions between any two entities
        for (Entity e1 : entities) {
            for (Entity e2 : entities) {
                if(!e1.equals(e2)) {
                    e1.checkCollision(e2);
                }
            }
        }
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

        for (StaticEntity e : staticEntities) {
            Sprite sprite = e.getSprite();
            sprite.setRegion(e.getTexture());
            sprite.setPosition(e.getX(), e.getY());
            sprite.draw(batch);

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


