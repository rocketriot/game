package bham.bioshock.client.screens;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.Asteroid;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.models.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import bham.bioshock.client.controllers.GameBoardController;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.Random;

public class GameBoardScreen extends ScreenMaster {
    private float coordRatio;
    private GameBoardController controller;
    private SpriteBatch batch;
    private Texture background;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer sh;
    private ArrayList<Sprite> planetSprites;
    private ArrayList<Sprite> asteroidSprites;
    private Sprite sprite;
    private ArrayList<Sprite> playerSprites;

    public GameBoardScreen(final GameBoardController controller) {
        this.controller = controller;
        batch = new SpriteBatch();


        int screenwidth = 1920;
        int screenheight = 1080;
        coordRatio = screenheight / 36f;

        camera = new OrthographicCamera();
        viewport = new FitViewport(screenwidth, screenheight, camera);
        viewport.apply();

        Graphics.DisplayMode display = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setFullscreenMode(display);

        background = new Texture(Gdx.files.internal("app/assets/backgrounds/game.png"));

        genAsteroidSprites();
        genPlanetSprites();
        genPlayerSprites();

        stage = new Stage(viewport, batch);

    }

    public void drawBoardObjects() {
        GridPoint[][] grid = controller.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                GridPoint.Type pType = grid[x][y].getType();
                if (pType == GridPoint.Type.PLAYER) {
                    Player p = (Player) grid[x][y].getValue();
                    sprite = playerSprites.get(p.getTextureID());
                    float xCoord = p.getCoordinates().getX();
                    float yCoord = p.getCoordinates().getY();
                    sprite.setX(xCoord * coordRatio);
                    sprite.setY(yCoord * coordRatio);
                    sprite.draw(stage.getBatch());
                } else if (pType == GridPoint.Type.PLANET) {
                    Planet p = (Planet) grid[x][y].getValue();
                    if (p.isDrawn() == false) {
                        p.setDrawn(true);
                        sprite = planetSprites.get(p.getTextureID());
                        float xCoord = p.getCoordinates().getX();
                        float yCoord = p.getCoordinates().getY();
                        sprite.setX(xCoord * coordRatio);
                        sprite.setY(yCoord * coordRatio);
                        sprite.draw(stage.getBatch());
                    }
                } else if (pType == GridPoint.Type.ASTEROID) {
                    Asteroid a = (Asteroid) grid[x][y].getValue();
                    if (a.isDrawn() == false) {
                        a.setDrawn(true);
                        sprite = asteroidSprites.get(a.getTextureID());
                        float xCoord = a.getCoordinates().getX();
                        float yCoord = a.getCoordinates().getY();
                        sprite.setX(xCoord * coordRatio);
                        sprite.setY(yCoord * coordRatio);
                        sprite.draw(stage.getBatch());
                    }
                }
            }
        }
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                GridPoint.Type pType = grid[x][y].getType();
                if (pType == GridPoint.Type.PLANET) {
                    Planet p = (Planet) grid[x][y].getValue();
                    p.setDrawn(false);
                } else if (pType == GridPoint.Type.ASTEROID) {
                    Asteroid a = (Asteroid) grid[x][y].getValue();
                    a.setDrawn(false);
                }
            }
        }
    }

    public ArrayList<Sprite> genPlanetSprites() {
        planetSprites = new ArrayList<>();
        FileHandle[] fh = Gdx.files.internal("app/assets/entities/planets").list();
        for (FileHandle f : fh) {
            Texture planetTexture = new Texture(Gdx.files.internal(f.path()));
            planetSprites.add(new Sprite(planetTexture));
        }
        return planetSprites;
    }

    public ArrayList<Sprite> genAsteroidSprites() {
        asteroidSprites = new ArrayList<>();
        FileHandle[] fh = Gdx.files.internal("app/assets/entities/asteroids").list();
        for (FileHandle f : fh) {
            Texture asteroidTexture = new Texture(Gdx.files.internal(f.path()));
            asteroidSprites.add(new Sprite(asteroidTexture));
        }
        return asteroidSprites;
    }

    public ArrayList<Sprite> genPlayerSprites() {
        playerSprites = new ArrayList<>();
        FileHandle[] fh = Gdx.files.internal("app/assets/entities/rockets").list();
        for (FileHandle f : fh) {
            Texture asteroidTexture = new Texture(Gdx.files.internal(f.path()));
            playerSprites.add(new Sprite(asteroidTexture));
        }
        return playerSprites;
    }

    public void drawGridLines() {
        sh = new ShapeRenderer();
        sh.setProjectionMatrix(camera.combined);
        sh.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        sh.setColor(211, 211, 211, 0.4f);
        for (int i = 0; i < 37; i++) {
            if (i == 0) {
                sh.line(1, 0, 1, Gdx.graphics.getHeight());
            }
            sh.line(i * coordRatio, 0, i * coordRatio, Gdx.graphics.getHeight());
        }
        for (int i = 0; i < 37; i++) {
            if (i == 0) {
                sh.line(0, 1, Gdx.graphics.getHeight(), 1);
            }
            sh.line(0, i * coordRatio, Gdx.graphics.getHeight(), i * coordRatio);
        }
        sh.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);
    }

    @Override
    public void show() {

    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getBatch().setProjectionMatrix(camera.combined);
        coordRatio = height / 36f;
        System.out.println(height);
        resizeSprites();
        viewport.update(width, height);
    }

    private void resizeSprites() {
        for (Sprite s : planetSprites) {
            s.setSize(coordRatio * 3, coordRatio * 3);
        }
        for (Sprite s : asteroidSprites) {
            s.setSize(coordRatio * 4, coordRatio * 3);
        }
        for (Sprite s : playerSprites) {
            s.setSize(coordRatio * 1, coordRatio * 1);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0);
        drawBoardObjects();
        stage.getBatch().end();
        stage.draw();
        drawGridLines();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        background.dispose();
        sh.dispose();
        for (Sprite s : planetSprites) {
            s.getTexture().dispose();
        }

        for (Sprite s : asteroidSprites) {
            s.getTexture().dispose();
        }

        for (Sprite s : playerSprites) {
            s.getTexture().dispose();
        }
    }

}
