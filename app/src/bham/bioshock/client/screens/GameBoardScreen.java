package bham.bioshock.client.screens;

import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
import bham.bioshock.common.pathfinding.AStarPathfinding;
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
  /** The game data */
  private Store store;

  /** Pathfinding for player movement */
  private AStarPathfinding pathFinder;
  private ArrayList<Coordinates> path = new ArrayList<>();
  
  private final InputMultiplexer inputMultiplexer;
  private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
  private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;


  private SpriteBatch batch;
  private Sprite background;
  private OrthographicCamera camera;
  private FitViewport viewport;
  private ShapeRenderer sh;
  private Sprite sprite;
  private ArrayList<Sprite> planetSprites;
  private ArrayList<Sprite> asteroidSprites;
  private ArrayList<Sprite> playerSprites;
  private ArrayList<Sprite> outlinedPlayerSprites;
  private Sprite fuelSprite;

  /** Pixels Per Square (on the grid) */
  private int PPS = 50;

  /** Size of the board */
  private int gridSize;

  private Hud hud;
  private int mouseDownX, mouseDownY;
  private boolean playerSelected = false;
  private Coordinates oldGridCoords = new Coordinates(-1, -1);

  public GameBoardScreen(Router router, Store store) {
    super(router);

    this.store = store;

    this.batch = new SpriteBatch();

    this.gridSize = store.getGameBoard().GRID_SIZE;
    this.camera = new OrthographicCamera();
    this.viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, camera);
    this.viewport.apply();

    // Generate the sprites
    this.planetSprites = generateSprites("app/assets/entities/planets");
    this.playerSprites = generateSprites("app/assets/entities/players");
    this.outlinedPlayerSprites = generateSprites("app/assets/entities/players");
    this.asteroidSprites = generateSprites("app/assets/entities/asteroids");
    this.fuelSprite = generateSprite("app/assets/entities/fuel.png");
    
    setupUI();
    
    // Setup the input processing
    this.inputMultiplexer = new InputMultiplexer();
    this.inputMultiplexer.addProcessor(hud.getStage());
    this.inputMultiplexer.addProcessor(this);
    
    this.sh = new ShapeRenderer();
  }

  private void setupUI() {
    hud = new Hud(batch, skin, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, store, router);
    background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));
  }

  public void drawBoardObjects() {
    GridPoint[][] grid = store.getGameBoard().getGrid();

    // Draw Grid items
    for (int x = 0; x < grid.length; x++) {
      for (int y = 0; y < grid[x].length; y++) {
        // Get grid point type
        GridPoint.Type type = grid[x][y].getType();

        switch (type) {
          case PLANET:
            Planet planet = (Planet) grid[x][y].getValue();

            // Check if planet has already been drawn
            if (!planet.getCoordinates().isEqual(new Coordinates(x, y)))
              continue;
            
            sprite = planetSprites.get(planet.getTextureID());
            break;

          case ASTEROID:
            Asteroid asteroid = (Asteroid) grid[x][y].getValue();

            // Check if asteroid has already been drawn
            if (!asteroid.getCoordinates().isEqual(new Coordinates(x, y)))
              continue;
  
            sprite = asteroidSprites.get(asteroid.getTextureID());
            break;

          case FUEL:
            sprite = fuelSprite;
            break;
          
          case EMPTY:
            continue;

          default:
            break;
        }

        // Draw Sprite
        sprite.setX(x * PPS);
        sprite.setY(y * PPS);
        sprite.draw(batch);
      }
    }

    // Draw players
    for (Player player : store.getPlayers()) {
      sprite = playerSprites.get(player.getTextureID());

      if (playerSelected == true && player.equals(store.getMainPlayer()))
        sprite = outlinedPlayerSprites.get(player.getTextureID());

      sprite.setX(player.getCoordinates().getX() * PPS);
      sprite.setY(player.getCoordinates().getY() * PPS);
      sprite.draw(batch);
    }
  }

  /** Generates an array of sprites from a folder */
  public ArrayList<Sprite> generateSprites(String path) {
    ArrayList<Sprite> sprites = new ArrayList<>();
    FileHandle[] fileHandle = Gdx.files.internal(path).list();

    for (FileHandle file : fileHandle) {
      sprite = generateSprite(file.path());
      sprites.add(sprite);
    }

    return sprites;
  }

  /** Generates a sprite from a file */
  public Sprite generateSprite(String path) {
    FileHandle file = Gdx.files.internal(path);
    Texture texture = new Texture(file);
    return new Sprite(texture);
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
    Gdx.input.setInputProcessor(inputMultiplexer);
  }

  @Override
  public void hide() {
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

    fuelSprite.setSize(PPS * 1, PPS * 1);
  }

  @Override
  public void render(float delta) {
    batch.setProjectionMatrix(camera.combined);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    handleInput();
    camera.update();

    
    batch.begin();
    
    drawBackground();
    drawBoardObjects();
    drawPath();
    
    batch.end();

    drawGridLines();
    

    // Draw the ui
    this.batch.setProjectionMatrix(hud.stage.getCamera().combined);
    hud.getStage().act(Gdx.graphics.getDeltaTime());
    hud.updateHud();
    hud.getStage().draw();
  }

  public boolean[] getPathColour(ArrayList<Coordinates> path) {
    boolean[] allowedMove = new boolean[path.size()];
    float fuel = store.getMainPlayer().getFuel();
    for (int i = 0; i < path.size(); i++) {
      if (fuel < 10f) {
        allowedMove[i] = false;
      } else {
        allowedMove[i] = true;
        fuel -= 10;
      }
    }
    return allowedMove;
  }

  private void drawPath() {
    if (playerSelected == true) {
      sh.setProjectionMatrix(camera.combined);
      sh.begin(ShapeRenderer.ShapeType.Filled);
      Gdx.gl.glEnable(GL30.GL_BLEND);
      Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

      boolean[] allowedPath = getPathColour(path);

      // Draw white box at player position
      sh.setColor(255, 255, 255, 0.4f);
      Coordinates playerCoords = store.getMainPlayer().getCoordinates();
      sh.rect(PPS * playerCoords.getX(), PPS * playerCoords.getY(), PPS, PPS);
      // Draw Path
      for (int i = 1; i < path.size(); i++) {
        if (allowedPath[i - 1] == false) {
          // Red
          sh.setColor(255, 0, 0, 0.5f);
        } else if (allowedPath[i - 1] == true) {
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
      ArrayList<Player> players = store.getPlayers();
      Player player = players.get(0);

      if (clickCoords.x >= player.getCoordinates().getX() * PPS
          && clickCoords.x <= (player.getCoordinates().getX() + 1) * PPS) {
        if (clickCoords.y >= player.getCoordinates().getY() * PPS
            && clickCoords.y <= (player.getCoordinates().getY() + 1) * PPS) {
          playerSelected = true;
          path = new ArrayList<>();
        }
      }
      return true;
    } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
      Coordinates gridCoords =
          new Coordinates((int) clickCoords.x / PPS, (int) clickCoords.y / PPS);
      if (!store.getMainPlayer().getCoordinates().isEqual(gridCoords)) {
        // controller.move(gridCoords);
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
      Coordinates gridCoords =
          new Coordinates((int) mouseCoords.x / PPS, (int) mouseCoords.y / PPS);
      if (!oldGridCoords.isEqual(gridCoords)) {
        if (gridCoords.getX() < gridSize - 1 && gridCoords.getX() >= 0) {
          if (gridCoords.getY() < gridSize - 1 && gridCoords.getY() >= 0) {
            if (!gridCoords.isEqual(store.getMainPlayer().getCoordinates())) {
              path = pathFinder.pathfind(gridCoords);
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
