package bham.bioshock.client.screens;

import bham.bioshock.client.controllers.GameBoardController;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.Asteroid;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.models.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class GameBoardScreen extends ScreenMaster implements InputProcessor {
    private float aspectRatio;
    private GameBoardController controller;
    private SpriteBatch batch;
    private Sprite background;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer sh;
    private ArrayList<Sprite> planetSprites;
    private ArrayList<Sprite> asteroidSprites;
    private Sprite sprite;
    private ArrayList<Sprite> playerSprites;
    private int PPS;
    private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
    private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
    private int gridHeight, gridWidth, gridSize;
    private Hud hud;
    private int mouseDownX, mouseDownY;

    public GameBoardScreen(final GameBoardController controller) {
        this.controller = controller;
        batch = new SpriteBatch();
        hud = new Hud(batch, skin, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, controller);

        gridWidth = GAME_WORLD_WIDTH - (GAME_WORLD_WIDTH % 36);
        System.out.println(gridWidth);
        gridHeight = gridWidth;

        // Pixels Per Square (on the grid)
        PPS = 50;

        gridSize = controller.getGrid().length;

        aspectRatio = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
        camera = new OrthographicCamera();
        viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, camera);
        viewport.apply();

        genAsteroidSprites();
        genPlanetSprites();
        genPlayerSprites();

        setupUI();

    }

    public void updateGrid(GridPoint[][] grid) {
        // TODO: handles when the grid changes
    }

    private void setupUI() {
        stage = new Stage(viewport, batch);
        background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));
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
                    sprite.setX(xCoord * PPS);
                    sprite.setY(yCoord * PPS);
                    sprite.draw(stage.getBatch());
                } else if (pType == GridPoint.Type.PLANET) {
                    Planet p = (Planet) grid[x][y].getValue();
                    if (p.isDrawn() == false) {
                        p.setDrawn(true);
                        sprite = planetSprites.get(p.getTextureID());
                        float xCoord = p.getCoordinates().getX();
                        float yCoord = p.getCoordinates().getY();
                        sprite.setX(xCoord * PPS);
                        sprite.setY(yCoord * PPS);
                        sprite.draw(stage.getBatch());
                    }
                } else if (pType == GridPoint.Type.ASTEROID) {
                    Asteroid a = (Asteroid) grid[x][y].getValue();
                    if (a.isDrawn() == false) {
                        a.setDrawn(true);
                        sprite = asteroidSprites.get(a.getTextureID());
                        float xCoord = a.getCoordinates().getX();
                        float yCoord = a.getCoordinates().getY();
                        sprite.setX(xCoord * PPS);
                        sprite.setY(yCoord * PPS);
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
        for (int i = 0; i < gridSize + 1; i++) {
            if (i == 0) {
                sh.line(0, 0, 0, (gridSize) * PPS);
            }
            sh.line(i * PPS, 0, i * PPS, (gridSize) * PPS);
        }
        for (int i = 0; i < gridSize + 1; i++) {
            if (i == 0) {
                sh.line(0, 0, (gridSize) * PPS, 0);
            }
            sh.line(0, i * PPS, (gridSize) * PPS, i * PPS);
        }
        sh.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);
    }

    @Override
    public void show() {
        //Graphics.DisplayMode display = Gdx.graphics.getDisplayMode();
        //Gdx.graphics.setFullscreenMode(display);
        Gdx.input.setInputProcessor(this);
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Graphics.DisplayMode display = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        resizeSprites();
    }

    private void resizeSprites() {
        for (Sprite s : planetSprites) {
            s.setSize(PPS * 3, PPS * 3);
        }
        for (Sprite s : asteroidSprites) {
            s.setSize(PPS * 4, PPS * 3);
        }
        for (Sprite s : playerSprites) {
            s.setSize(PPS * 1, PPS * 1);
        }
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        camera.update();

        stage.getBatch().begin();
        drawBackground();
        drawBoardObjects();
        stage.getBatch().end();
        drawGridLines();
        stage.act(Gdx.graphics.getDeltaTime());

        // Draw the ui
        this.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    protected void drawBackground() {

        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                background.setPosition(i * GAME_WORLD_WIDTH, j* GAME_WORLD_HEIGHT);
                background.draw(batch);
            }
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        hud.dispose();
        background.getTexture().dispose();
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

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.translate(-5f, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.translate(5f, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.translate(0f, 5f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.translate(0f, -5f);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mouseDownX = screenX;
        mouseDownY = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        camera.translate(-(screenX - mouseDownX), screenY - mouseDownY);
        mouseDownX = screenX;
        mouseDownY = screenY;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if ((PPS -= amount * 1) <= ((GAME_WORLD_HEIGHT / gridSize) - 4)) {
            PPS = (GAME_WORLD_HEIGHT / gridSize) - 3;
        } else if (PPS < 30) {
            PPS -= amount * 1;
        } else if (PPS < 50) {
            PPS -= amount * 2;
        } else if (PPS < 70) {
            PPS -= amount * 3;
        }  else if (PPS >= 70) {
            if ((PPS -= amount * 4) >= 150) {
                PPS = 149;
            } else {
                PPS -= amount * 3;
            }
        }
        resizeSprites();
        return false;
    }
}
