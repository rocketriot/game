package bham.bioshock.client.screens;

import bham.bioshock.client.Assets;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.gameboard.DrawAsteroid;
import bham.bioshock.client.scenes.gameboard.DrawFuel;
import bham.bioshock.client.scenes.gameboard.DrawPlanet;
import bham.bioshock.client.scenes.gameboard.DrawPlayer;
import bham.bioshock.client.scenes.gameboard.PathRenderer;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.Direction;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
import bham.bioshock.common.models.store.Store;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameBoardScreen extends ScreenMaster implements InputProcessor {
  private final InputMultiplexer inputMultiplexer;
  private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
  private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
  
  /** The game data */
  private Store store;
  
  private SpriteBatch batch;
  private Sprite background;
  private OrthographicCamera camera;
  private FitViewport viewport;
  private ShapeRenderer sr;

  /** Pixels Per Square (on the grid) */
  private int PPS = 27;

  /** Size of the board */
  private int gridSize;

  private Hud hud;
  private int mouseDownX, mouseDownY;
  private boolean playerSelected = false;
  private Coordinates oldGridCoords = new Coordinates(-1, -1);
  // private Array<ParticleEffect> effects = new Array<>();
  // private ParticleEffect rocketTrail;

  DrawPlayer drawPlayer;
  DrawPlanet drawPlanet;
  DrawFuel drawFuel;
  DrawAsteroid drawAsteroid;
  PathRenderer pathRenderer;

  private final float CAMERA_MOVE_SPEED = 5f;

  public GameBoardScreen(Router router, Store store) {
    super(router);

    this.store = store;
    this.gridSize = store.getGameBoard().GRID_SIZE;

    this.camera = new OrthographicCamera();
    this.batch = new SpriteBatch();
    this.sr = new ShapeRenderer();


    this.viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, camera);
    this.viewport.apply();

    drawPlayer = new DrawPlayer(batch);
    drawPlanet = new DrawPlanet(batch);
    drawFuel = new DrawFuel(batch);
    drawAsteroid = new DrawAsteroid(batch);

    pathRenderer = new PathRenderer(camera, store.getGameBoard(), store.getMainPlayer(), store.getPlayers());

    // generateEffects();

    hud = new Hud(batch, skin, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, store, router);
    background = new Sprite(new Texture(Gdx.files.internal(Assets.gameBackground)));

    // Setup the input processing
    this.inputMultiplexer = new InputMultiplexer();
    this.inputMultiplexer.addProcessor(hud.getStage());
    this.inputMultiplexer.addProcessor(this);
  }

  /** Draws the player move */
  private void drawPlayerMove(Player player) {
    GameBoard gameBoard = store.getGameBoard();
    ArrayList<Player.Move> boardMove = player.getBoardMove();
    
    // Handle end of movement
    if (boardMove.size() == 0) {
      player.clearBoardMove();

      // Show minigame prompt if next to planet
      if (gameBoard.isNextToThePlanet(player.getCoordinates())) {
        showMinigamePrompt();
      }

      return;
    }

    // Handle move start
    if (boardMove.get(0).getDirection() == Direction.NONE) {
      // Clears the path and unselects the player
      playerSelected = false;
      pathRenderer.clearPath(); 
    }

    
    // Draw the updated player
    boolean didChangeCoordinates = drawPlayer.drawMove(player, PPS);
    
    // Update the players coordinates if the player has moved 1 position
    if (didChangeCoordinates) {
      Coordinates nextCoordinates = boardMove.get(0).getCoordinates();
      player.setCoordinates(nextCoordinates);

      // Remove the completed move
      boardMove.remove(0);
    }
    
    // Get the value of the grid point that the player has landed on
    GridPoint gridPoint = gameBoard.getGridPoint(player.getCoordinates());

    // Check if the grid point is fuel
    if (gridPoint.getType() == GridPoint.Type.FUEL) {
      // Increase the players fuel
      Fuel fuel = (Fuel) gridPoint.getValue();
      player.increaseFuel(fuel.getValue());

      // Remove fuel from the grid
      gameBoard.removeGridPoint(player.getCoordinates());
    }
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

            // Only draw the planet from the bottom left coordinate
            if (planet.getCoordinates().isEqual(new Coordinates(x, y)))
              drawPlanet.draw(planet, PPS);
            
            break;

          case ASTEROID:
            Asteroid asteroid = (Asteroid) grid[x][y].getValue();

            // Only draw the asteroid from the bottom left coordinate
            if (asteroid.getCoordinates().isEqual(new Coordinates(x, y)))
              drawAsteroid.draw(asteroid, PPS);

            break;

          case FUEL:
            Fuel fuel = (Fuel) grid[x][y].getValue();
            drawFuel.draw(fuel, PPS);
            break;

          default:
            break;
        }
      }
    }
  }

  private void drawPlayers() {
    for (Player player : store.getPlayers()) {
      // Handle if player is moving
      if (player.getBoardMove() != null) {
        drawPlayerMove(player);
        continue;
      }

      boolean isMainPlayer = player.equals(store.getMainPlayer());
      drawPlayer.draw(player, PPS, isMainPlayer);
    }
  }

  private void drawGridLines() {
    sr.setProjectionMatrix(camera.combined);
    sr.begin(ShapeRenderer.ShapeType.Line);
    sr.setColor(211, 211, 211, 0.2f);

    Gdx.gl.glEnable(GL30.GL_BLEND);
    Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    
    for (int i = 0; i < gridSize + 1; i++)
      sr.line(i * PPS, 0, i * PPS, (gridSize) * PPS);
    
    for (int i = 0; i < gridSize + 1; i++)
      sr.line(0, i * PPS, (gridSize) * PPS, i * PPS);

    sr.end();

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
    drawPlayer.resize(PPS);
    drawPlanet.resize(PPS);
    drawFuel.resize(PPS);
    drawAsteroid.resize(PPS);

    background.setSize(PPS * 38.4f, PPS * 21.6f);
  }

  @Override
  public void render(float delta) {
    batch.setProjectionMatrix(camera.combined);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    handleKeyPress();
    camera.update();

    batch.begin();
    drawBackground();
    batch.end();
    
    drawGridLines();

    batch.begin();
    drawBoardObjects();
    drawPlayers();
    batch.end();

    // Shape render drawn methods
    pathRenderer.draw(PPS);

    // Draw the ui
    this.batch.setProjectionMatrix(hud.stage.getCamera().combined);
    hud.getStage().act(Gdx.graphics.getDeltaTime());
    hud.updateHud();
    hud.getStage().draw();
  }

  protected void drawBackground() {
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        background.setPosition(i * background.getWidth(), j * background.getHeight());
        background.draw(batch);
      }
    }
  }

  @Override
  public void dispose() {
    batch.dispose();
    hud.dispose();
    background.getTexture().dispose();
    sr.dispose();

    drawPlayer.dispose();
    drawPlanet.dispose();
    drawFuel.dispose();
    drawAsteroid.dispose();
  }

  private Vector3 getMouseCoordinates(int screenX, int screenY) {
    Vector3 vector = new Vector3(screenX, screenY, 0);
    Vector3 coordinates = viewport.unproject(vector);
    
    return coordinates;
  }

  private void handleKeyPress() {
    // Unselect player on ESC
    if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) playerSelected = false;

    // Move camera with WASD
    if (Gdx.input.isKeyPressed(Input.Keys.W)) camera.translate(0f, CAMERA_MOVE_SPEED);
    if (Gdx.input.isKeyPressed(Input.Keys.A)) camera.translate(-CAMERA_MOVE_SPEED, 0f);
    if (Gdx.input.isKeyPressed(Input.Keys.S)) camera.translate(0f, -CAMERA_MOVE_SPEED);
    if (Gdx.input.isKeyPressed(Input.Keys.D)) camera.translate(CAMERA_MOVE_SPEED, 0f);
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {        
    // Used for mouse panning
    mouseDownX = screenX;
    mouseDownY = screenY;
    
    // Get mouse coordinates
    Vector3 mouse = getMouseCoordinates(screenX, screenY);

    if (!playerSelected) {
      startMove(mouse);
    } else {
      endMove(mouse);
    }

    return false;
  }

  private boolean startMove(Vector3 mouse) {
    // Check a left click was performed
    if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) return false;

    // Get player coordinates
    Player player = store.getMainPlayer();
    int playerX = player.getCoordinates().getX();
    int playerY = player.getCoordinates().getY();

    // Handle when mouse click is within player grid point
    if (
      playerX * PPS <= mouse.x && mouse.x <= (playerX + 1) * PPS && 
      playerY * PPS <= mouse.y && mouse.y <= (playerY + 1) * PPS
    ) {
        playerSelected = true;
        pathRenderer.clearPath();
      }

    return true;
  }

  private boolean endMove(Vector3 mouse) {
    // Check a left click was performed
    if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) return false;
    
    // Get players
    Player player = store.getMainPlayer();
    Player movingPlayer = store.getMovingPlayer();

    // Check it's the client's turn to move
    if (!movingPlayer.getId().equals(player.getId())) return false;
    
    // Check if click is on the grid
    if (
      0 <= mouse.x && mouse.x <= gridSize * PPS && 
      0 <= mouse.y && mouse.y <= gridSize * PPS
    ) {
      
      // Get new player coordinates
      int gridX = (int) mouse.x / PPS;
      int gridY = (int) mouse.y / PPS;
      Coordinates coordinates = new Coordinates(gridX, gridY);

      // Check that the player isn't attemping to move to it's current position
      if (!store.getMainPlayer().getCoordinates().isEqual(coordinates))
        router.call(Route.MOVE_PLAYER, coordinates);
    }

    return true;
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
    // Check if player is selected
    if (!playerSelected) return false;
    
    // Pathfind to mouse coordinates
    Vector3 mouseCoords = getMouseCoordinates(screenX, screenY);

      Coordinates gridCoords =
          new Coordinates((int) mouseCoords.x / PPS, (int) mouseCoords.y / PPS);
      if (!oldGridCoords.isEqual(gridCoords)) {
        if (gridCoords.getX() < gridSize && gridCoords.getX() >= 0) {
          if (gridCoords.getY() < gridSize && gridCoords.getY() >= 0) {
            if (!gridCoords.isEqual(store.getMainPlayer().getCoordinates())) {
              pathRenderer.generatePath(store.getMainPlayer().getCoordinates(), gridCoords);
              oldGridCoords = gridCoords;
              return true;
            }
          }
        }
      }

    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    // Zoom code
    if ((PPS -= amount) <= ((GAME_WORLD_HEIGHT / gridSize) - 4)) {
      PPS = (GAME_WORLD_HEIGHT / gridSize) - 3;
    } else if (PPS < 30) {
      PPS -= amount;
    } else if (PPS < 50) {
      PPS -= amount * 2;
    } else if (PPS < 70) {
      PPS -= amount * 3;
    } else {
      if ((PPS -= amount * 4) >= 150) {
        PPS = 149;
      } else {
        PPS -= amount * 3;
      }
    }
    resizeSprites();
    return false;
  }

  /** Method to ask the user whether they want to start the minigame or not */
  private void showMinigamePrompt() {
    Dialog dialog =
        new Dialog("", skin) {

          protected void result(Object object) {

            if (object.equals(true)) {
              System.out.println("Starting minigame");
              router.call(Route.START_MINIGAME);
            } else {
              System.out.println("Minigame not started");
            }
          }
        };

    dialog.text(new Label("Do you want to attempt to capture this planet?", skin));
    dialog.button("Yes", true);
    dialog.button("No", false);

    dialog.show(hud.getStage());
  }
}
