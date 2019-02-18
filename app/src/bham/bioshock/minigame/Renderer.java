package bham.bioshock.minigame;


import java.awt.*;
import java.util.ArrayList;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;

import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.Clock.TimeUpdateEvent;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Map;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.physics.Gravity;
import bham.bioshock.minigame.worlds.FirstWorld;
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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class Renderer {
    private FirstWorld World;
    private Player mainPlayer;
    private ArrayList<Entity> entities;

    ShapeRenderer renderer;
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
    private Clock clock;
    private MinigameStore store;
    private Gravity gravity;
    private Router router;


    public Renderer(MinigameStore store, Router router) {
        this.store = store;
        this.router = router;
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
//    startClock();
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

    public void render(float delta) {

        //    clock.update(delta);
        batch.setProjectionMatrix(cam.combined);
        renderer.setProjectionMatrix(cam.combined);


        updatePosition();
        cam.position.lerp(lerpTarget.set(mainPlayer.getX(), mainPlayer.getY(), 0), 3f * delta);

        double rotation = -Gravity.getAngleTo(cam.position.x, cam.position.y);
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

        Rectangle border = mainPlayer.getRectangle();
        // drawBorder(border);
        batch.end();
    }


    public void drawPlanet() {
        renderer.begin(ShapeType.Filled);
        renderer.setColor(Color.SALMON);

        renderer.circle(0, 0, (float) World.getPlanetRadius());
        // bounding circle
        mainPlanet = new Circle(0, 0, (float) World.getPlanetRadius() - 50);
        // renderer.setColor(Color.RED);
        // renderer.circle(0,0,(float)World.PLANET_RADIUS-50);

        renderer.end();
    }


    public void drawBorder(Rectangle border) {
        renderer.begin(ShapeType.Filled);
        renderer.setColor(Color.BLACK);
        renderer.rect(border.getX(), border.getY(), border.getWidth(), border.getHeight());
        renderer.end();
    }

    public void drawEntities() {
        for (Player p : store.getPlayers()) {
            Sprite sprite = p.getSprite();
            sprite.setRegion(p.getTexture());
            Rectangle border = p.getRectangle();
            sprite.setPosition(p.getX(), p.getY());
            sprite.setRotation((float) p.getRotation());
            sprite.draw(batch);
            p.update(Gdx.graphics.getDeltaTime());
            // drawBorder(border);
        }
        for (Rocket p : store.getRockets()) {
            Sprite sprite = p.getSprite();
            Rectangle border = p.getRectangle();
            sprite.setRegion(p.getTexture());

            sprite.setPosition(p.getX(), p.getY());
            sprite.setRotation((float) p.getRotation());
            sprite.draw(batch);


            p.update(Gdx.graphics.getDeltaTime());

            map.addRocket(border);
            // drawBorder(border);

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


    public void updatePosition() {
        float dt = Gdx.graphics.getDeltaTime();
        mainPlayer.update(dt);
        boolean moveMade = false;

        // System.out.println(mainPlayer.getRectangle());
        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) && !mainPlayer.colLeft) {
            moveMade = true;

            mainPlayer.moveLeft(dt);

            if (collidesWithFreeRocket(mainPlayer.getRectangle())) {

                if (mainPlayer.colRight) {

                    mainPlayer.moveLeft(dt);
                } else
                    mainPlayer.colLeft = true;

            } else {
                mainPlayer.colRight = false;
                mainPlayer.col(dt);
            }


        }


        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) && !mainPlayer.colRight) {
            moveMade = true;
            mainPlayer.moveRight(dt);

            if (collidesWithFreeRocket(mainPlayer.getRectangle())) {

                if (mainPlayer.colLeft) {
                    System.out.println("stop left");

                    mainPlayer.moveRight(dt);
                } else
                    mainPlayer.colRight = true;

            } else {

                mainPlayer.colLeft = false;
                mainPlayer.col(dt);

            }

        }


        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveMade = true;
            mainPlayer.jump(dt);
        }

        if (moveMade) {
            router.call(Route.MINIGAME_MOVE);
        }


    }


    public void drawMainPlayer() {
        Sprite sprite = mainPlayer.getSprite();
        sprite.setRegion(mainPlayer.getTexture());
        sprite.setPosition(mainPlayer.getX(), mainPlayer.getY());
        sprite.setRotation((float) -mainPlayer.angleFromCenter());

        sprite.draw(batch);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public boolean collidesWithFreeRocket(Rectangle r) {

        ArrayList<Rectangle> rockets = map.getRockets();


        for (int i = 0; i < rockets.size(); i++) {
            if (Intersector.overlaps(r, rockets.get(i))) {
                return true;
            }
        }
        return false;
    }
}


