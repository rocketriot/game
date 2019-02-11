package bham.bioshock.client.screens;

import bham.bioshock.client.controllers.GameBoardController;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class GameBoardScreen extends ScreenMaster implements InputProcessor {
    private final InputMultiplexer inputMultiplexer;
    private GameBoardController controller;
    private SpriteBatch batch;
    private Sprite background;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer sh;
    private ArrayList<Sprite> planetSprites;
    private ArrayList<Sprite> asteroidSprites;
    private ArrayList<Coordinates> drawMoveCoords;
    private Sprite sprite;
    private ArrayList<Sprite> playerSprites;
    private int PPS;
    private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
    private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
    private int gridSize;
    private Hud hud;
    private int mouseDownX, mouseDownY;
    private boolean playerSelected = false;
    private ArrayList<Sprite> outlinedPlayerSprites;
    private ArrayList<Coordinates> path = new ArrayList<>();
    private Coordinates oldGridCoords = new Coordinates(-1, -1);
    private Sprite movingSprite;
    private boolean firstMove;

    public GameBoardScreen(final GameBoardController controller) {
        this.controller = controller;
        batch = new SpriteBatch();

        // Pixels Per Square (on the grid)
        PPS = 50;

        gridSize = controller.getGridSize();
        camera = new OrthographicCamera();
        viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, camera);
        viewport.apply();

        // Generate the arraylists of sprites
        genAsteroidSprites();
        genPlanetSprites();
        genPlayerSprites();

        setupUI();

        // Setup the input processing
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud.getStage());
        inputMultiplexer.addProcessor(this);

        sh = new ShapeRenderer();
    }

    private void setupUI() {
        hud = new Hud(batch, skin, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, controller);
        background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));
    }

    /**
     * Draws the player move
     */
    public void drawPlayerMove() {
        Player player = controller.getMainPlayer();
        BoardMove boardMove = player.getBoardMove();
        if (boardMove != null) {
            if (boardMove.getDirections().size() == 0) {
                firstMove = true;
                player.setBoardMove(null);
            }

            float distanceToMove = 100 * Gdx.graphics.getDeltaTime();
            movingSprite = planetSprites.get(player.getTextureID());
            if (firstMove == true) {
                sprite.setX(boardMove.getStartCoords().getX() * PPS);
                sprite.setY(boardMove.getStartCoords().getY() * PPS);
                firstMove = false;
            }
            switch (boardMove.getDirections().get(0)) {
                case UP:
                    movingSprite.translateY(distanceToMove);
                    if (movingSprite.getY() >= boardMove.getPosition().get(0).getY()) {
                        movingSprite.setY(boardMove.getPosition().get(0).getY());
                        boardMove.getPosition().remove(0);
                        boardMove.getDirections().remove(0);
                    }
                case DOWN:
                    movingSprite.translateY(-distanceToMove);
                    if (movingSprite.getY() <= boardMove.getPosition().get(0).getY()) {
                        movingSprite.setY(boardMove.getPosition().get(0).getY());
                        boardMove.getPosition().remove(0);
                        boardMove.getDirections().remove(0);
                    }
                case RIGHT:
                    movingSprite.translateX(distanceToMove);
                    if (movingSprite.getX() >= boardMove.getPosition().get(0).getX()) {
                        movingSprite.setX(boardMove.getPosition().get(0).getX());
                        boardMove.getPosition().remove(0);
                        boardMove.getDirections().remove(0);
                    }
                case LEFT:
                    movingSprite.translateX(-distanceToMove);
                    if (movingSprite.getX() <= boardMove.getPosition().get(0).getX()) {
                        movingSprite.setX(boardMove.getPosition().get(0).getX());
                        boardMove.getPosition().remove(0);
                        boardMove.getDirections().remove(0);
                    }
            }
            System.out.println(movingSprite.getX() + ", " + movingSprite.getY());
            movingSprite.draw(batch);
        }
    }

    public void drawBoardObjects() {
        GridPoint[][] grid = controller.getGrid();

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                GridPoint.Type pType = grid[x][y].getType();
                if (pType == GridPoint.Type.PLAYER) {
                    Player p = (Player) grid[x][y].getValue();

                    //TODO remove code once player is sent
                    p.setCoordinates(new Coordinates(x, y));

                    if (playerSelected == true && p.equals(controller.getMainPlayer())) {
                        sprite = outlinedPlayerSprites.get(p.getTextureID());
                    } else {
                        sprite = playerSprites.get(p.getTextureID());
                    }
                    sprite.setX(x * PPS);
                    sprite.setY(y * PPS);
                    sprite.draw(batch);
                } else if (pType == GridPoint.Type.PLANET) {
                    Planet p = (Planet) grid[x][y].getValue();
                    if (p.isDrawn() == false) {
                        p.setDrawn(true);
                        sprite = planetSprites.get(p.getTextureID());
                        sprite.setX(x * PPS);
                        sprite.setY(y * PPS);
                        sprite.draw(batch);
                    }
                } else if (pType == GridPoint.Type.ASTEROID) {
                    Asteroid a = (Asteroid) grid[x][y].getValue();
                    if (a.isDrawn() == false) {
                        a.setDrawn(true);
                        sprite = asteroidSprites.get(a.getTextureID());
                        sprite.setX(x * PPS);
                        sprite.setY(y * PPS);
                        sprite.draw(batch);
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

    public void genPlanetSprites() {
        planetSprites = new ArrayList<>();
        FileHandle[] fh = Gdx.files.internal("app/assets/entities/planets").list();
        for (FileHandle f : fh) {
            Texture planetTexture = new Texture(Gdx.files.internal(f.path()));
            planetSprites.add(new Sprite(planetTexture));
        }
    }

    public void genAsteroidSprites() {
        asteroidSprites = new ArrayList<>();
        FileHandle[] fh = Gdx.files.internal("app/assets/entities/asteroids").list();
        for (FileHandle f : fh) {
            Texture asteroidTexture = new Texture(Gdx.files.internal(f.path()));
            asteroidSprites.add(new Sprite(asteroidTexture));
        }
    }

    public void genPlayerSprites() {
        playerSprites = new ArrayList<>();
        FileHandle[] fh = Gdx.files.internal("app/assets/entities/rockets").list();
        for (FileHandle f : fh) {
            Texture playerTexture = new Texture(Gdx.files.internal(f.path()));
            playerSprites.add(new Sprite(playerTexture));
        }

        outlinedPlayerSprites = new ArrayList<>();
        fh = Gdx.files.internal("app/assets/entities/outlinedRockets").list();
        for (FileHandle f : fh) {
            Texture outlinedTexture = new Texture(Gdx.files.internal(f.path()));
            outlinedPlayerSprites.add(new Sprite(outlinedTexture));
        }
    }

    public void drawGridLines() {
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
        controller.onShow();

        // Graphics.DisplayMode display = Gdx.graphics.getDisplayMode();
        // Gdx.graphics.setFullscreenMode(display);
        Gdx.input.setInputProcessor(inputMultiplexer);
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
        hud.viewport.update(width, height, true);
        resizeSprites();
    }

    private void resizeSprites() {
        for (Sprite s : planetSprites) {
            s.setSize(PPS * 3, PPS * 3);
        }
        for (Sprite s : asteroidSprites) {
            s.setSize(PPS * 3, PPS * 4);
        }
        for (Sprite s : playerSprites) {
            s.setSize(PPS * 1, PPS * 1);
        }
        for (Sprite s : outlinedPlayerSprites) {
            s.setSize(PPS * 1, PPS * 1);
        }
    }

    @Override
    public void render(float delta) {
        if (controller.hasReceivedGrid() == true) {
            batch.setProjectionMatrix(camera.combined);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            handleInput();
            camera.update();

            batch.begin();

            drawBackground();
            drawBoardObjects();
            drawPath();
            drawPlayerMove();

            batch.end();

            drawGridLines();

            // Draw the ui
            this.batch.setProjectionMatrix(hud.stage.getCamera().combined);
            hud.getStage().act(Gdx.graphics.getDeltaTime());
            hud.updateHud();
            hud.getStage().draw();
        }
    }

    private void drawPath() {
        if (playerSelected == true && path != null) {
            sh.setProjectionMatrix(camera.combined);
            sh.begin(ShapeRenderer.ShapeType.Filled);
            Gdx.gl.glEnable(GL30.GL_BLEND);
            Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
            boolean[] allowedPath = controller.getPathColour(path);
            // Draw white box at player position
            sh.setColor(255, 255, 255, 0.4f);
            Coordinates playerCoords = controller.getMainPlayer().getCoordinates();
            sh.rect(PPS * playerCoords.getX(), PPS * playerCoords.getY(), PPS, PPS);
            // Draw Path
            for (int i = 1; i < path.size(); i++) {
                if (allowedPath[i - 1] == false) {
                    // Red
                    sh.setColor(255, 0, 0, 0.5f);
                } else if (allowedPath[i - 1] == true){
                    // Green
                    sh.setColor(124, 252, 0, 0.4f);
                }
                sh.rect(PPS * path.get(i).getX(), PPS * path.get(i).getY(), PPS, PPS);
            }
            sh.end();
            Gdx.gl.glDisable(GL30.GL_BLEND);
        }
    }


    protected void drawBackground() {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                background.setPosition(i * GAME_WORLD_WIDTH, j * GAME_WORLD_HEIGHT);
                background.draw(batch);
            }
        }

    }

    @Override
    public void dispose() {
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

        for (Sprite s : outlinedPlayerSprites) {
            s.getTexture().dispose();
        }
    }

    private Vector3 getWorldCoords(int screenX, int screenY) {
        Vector3 coords = new Vector3(screenX, screenY, 0);
        coords = viewport.unproject(coords);
        return coords;
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
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            playerSelected = false;
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
        Vector3 clickCoords = getWorldCoords(screenX, screenY);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // Used for mouse panning
            mouseDownX = screenX;
            mouseDownY = screenY;

            // Selecting your ship
            ArrayList<Player> players = controller.getPlayers();
            Player player = players.get(0);

            if (clickCoords.x >= player.getCoordinates().getX() * PPS && clickCoords.x <= (player.getCoordinates().getX() + 1) * PPS) {
                if (clickCoords.y >= player.getCoordinates().getY() * PPS && clickCoords.y <= (player.getCoordinates().getY() + 1) * PPS) {
                    playerSelected = true;
                    path = new ArrayList<>();
                }
            }
            return true;
        } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Coordinates gridCoords = new Coordinates((int) clickCoords.x / PPS, (int) clickCoords.y / PPS);
            if (!controller.getMainPlayer().getCoordinates().isEqual(gridCoords)) {
                controller.move(gridCoords);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Mouse camera panning
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            camera.translate(-(screenX - mouseDownX), screenY - mouseDownY);
            mouseDownX = screenX;
            mouseDownY = screenY;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Pathfind to mouse coordinates
        if (playerSelected == true) {
            Vector3 mouseCoords = getWorldCoords(screenX, screenY);
            Coordinates gridCoords = new Coordinates((int) mouseCoords.x / PPS, (int) mouseCoords.y / PPS);
            if (!oldGridCoords.isEqual(gridCoords)) {
                if (gridCoords.getX() < gridSize - 1 && gridCoords.getX() >= 0) {
                    if (gridCoords.getY() < gridSize - 1 && gridCoords.getY() >= 0) {
                        if (!gridCoords.isEqual(controller.getMainPlayer().getCoordinates())) {
                            path = controller.getPathFinder().pathfind(gridCoords);
                            oldGridCoords = gridCoords;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // Zoom code
        if ((PPS -= amount * 1) <= ((GAME_WORLD_HEIGHT / gridSize) - 4)) {
            PPS = (GAME_WORLD_HEIGHT / gridSize) - 3;
        } else if (PPS < 30) {
            PPS -= amount * 1;
        } else if (PPS < 50) {
            PPS -= amount * 2;
        } else if (PPS < 70) {
            PPS -= amount * 3;
        } else if (PPS >= 70) {
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
