package bham.bioshock.client.screens;

import bham.bioshock.client.Assets;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.client.gameLogic.gameboard.*;
import bham.bioshock.client.scenes.gameboard.hud.Hud;
import bham.bioshock.common.Direction;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;
import java.util.UUID;

public class GameBoardScreen extends ScreenMaster implements InputProcessor {
  private final InputMultiplexer inputMultiplexer;

  /** The speed at which to move the board with the WASD keys */
  private final float CAMERA_MOVE_SPEED = 5f;

  /** Draws players on the board */
  private DrawPlayer drawPlayer;

  /** Draws planets on the board */
  private DrawPlanet drawPlanet;

  /** Draws fuel on the board */
  private DrawFuel drawFuel;

  /** Draws asteroids on the board */
  private DrawAsteroid drawAsteroid;

  /** Handles the path rendering */
  private PathRenderer pathRenderer;

  /** The game data */
  private Store store;

  private SpriteBatch batch;
  private OrthographicCamera camera;
  private FitViewport viewport;
  private ShapeRenderer sr;

  /** Pixels Per Square (on the grid) */
  private int PPS = 27;

  /** Size of the board */
  private int gridSize;

  /** Used for displaying the current game statistics */
  private Hud hud;

  /** Used for mouse panning */
  private int mouseDownX, mouseDownY;

  /** The game background sprite */
  private Sprite background;

  /** Flag for if the player is selecting a move */
  private boolean playerSelected = false;

  public GameBoardScreen(Router router, Store store) {
    super(router);

    this.store = store;
    this.gridSize = store.getGameBoard().GRID_SIZE;

    this.camera = new OrthographicCamera();
    this.batch = new SpriteBatch();
    this.sr = new ShapeRenderer();

    this.viewport = new FitViewport(Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT, camera);
    this.viewport.apply();

    // Center the camera on the middle of the grid
    camera.position.set(Config.GAME_WORLD_WIDTH / 4, Config.GAME_WORLD_HEIGHT / 2.25f, 0);

    drawPlayer = new DrawPlayer(batch);
    drawPlanet = new DrawPlanet(batch);
    drawFuel = new DrawFuel(batch);
    drawAsteroid = new DrawAsteroid(batch);

    pathRenderer =
        new PathRenderer(camera, store.getGameBoard(), store.getMainPlayer(), store.getPlayers());

    hud = new Hud(batch, skin, store, router);
    background = new Sprite(new Texture(Gdx.files.internal(Assets.gameBackground)));

    // Setup the input processing
    this.inputMultiplexer = new InputMultiplexer();
    this.inputMultiplexer.addProcessor(hud.getStage());
    this.inputMultiplexer.addProcessor(this);
  }

  private boolean checkIfNearPlanet(Player player) {
    GameBoard gameBoard = store.getGameBoard();
    Planet p = gameBoard.getAdjacentPlanet(player.getCoordinates(), player);
    if (p == null)
      return false;
    else {
      showMinigamePrompt(p.getId());
      return true;
    }
  }

  /** Draws the player move */
  private void drawPlayerMove(Player player) {
    router.call(Route.LOOP_SOUND, "rocket");
    GameBoard gameBoard = store.getGameBoard();
    ArrayList<Player.Move> boardMove = player.getBoardMove();

    // Handle end of movement
    if (boardMove.size() == 0) {
      player.clearBoardMove();
      router.call(Route.STOP_SOUND, "rocket");

      if (store.isMainPlayersTurn()) {
        boolean nextTurn = true;

        // Only show minigame prompt and end turn if this client's player's turns
        if (store.isMainPlayersTurn() && player.equals(store.getMainPlayer())) {
          boolean shown = checkIfNearPlanet(player);
          if (shown) {
            nextTurn = false;
          }
        }
        if (nextTurn) {
          router.call(Route.END_TURN);
        }
      }

      return;
    }

    // Handle move start
    if (boardMove.get(0).getDirection() == Direction.NONE) {
      // Clears the path and unselects the player
      playerSelected = false;
      pathRenderer.clearPath();

      // Sets the intial draw values
      drawPlayer.setupMove(player);

      // Removing starting position from move
      boardMove.remove(0);
    }

    // Draw the updated player
    boolean didChangeCoordinates = drawPlayer.drawMove(player, PPS);

    // Update the players coordinates if the player has moved 1 position
    if (didChangeCoordinates) {
      Coordinates nextCoordinates = boardMove.get(0).getCoordinates();
      player.setCoordinates(nextCoordinates);

      // Remove the completed move
      boardMove.remove(0);

      // Decrease the player's fuel
      player.decreaseFuel(Player.FUEL_GRID_COST);
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

  private void drawGrid() {
    sr.setProjectionMatrix(camera.combined);
    sr.begin(ShapeRenderer.ShapeType.Filled);
    sr.setColor(new Color(0x213C69ff));

    for (int i = 0; i < gridSize + 1; i++) {
      // Outer grid lines are thicker
      boolean isOuterLine = i == 0 || i == gridSize;
      int lineThickness = isOuterLine ? (PPS / 4) : (PPS / 6);
      int offset = lineThickness / 2;

      // Draw horizontal lines
      sr.rect((i * PPS) - offset, 0 - offset, lineThickness, (gridSize * PPS) + lineThickness);

      // Draw vertical lines
      sr.rect(0 - offset, (i * PPS) - offset, (gridSize * PPS) + lineThickness, lineThickness);
    }

    sr.end();
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
    viewport.update(width, height);
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
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    batch.setProjectionMatrix(camera.combined);
    camera.update();

    handleKeyPress();

    // Draw background
    batch.begin();
    drawBackground();
    batch.end();

    // Draw board grid
    drawGrid();

    // Draw board entities
    batch.begin();
    drawBoardObjects();
    drawPlayers();
    batch.end();

    // Draw path rendering
    pathRenderer.draw(PPS);

    // Draw the HUD
    batch.setProjectionMatrix(hud.stage.getCamera().combined);
    hud.getStage().act(Gdx.graphics.getDeltaTime());
    hud.updateHud();
    hud.draw();
  }

  protected void drawBackground() {
    int offset = 1;
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 2; j++) {
        background.setPosition(i * background.getWidth() - offset++, j * background.getHeight());
        background.draw(batch);
      }
    }
  }

  @Override
  public void dispose() {
    batch.dispose();
    sr.dispose();

    drawPlayer.dispose();
    drawPlanet.dispose();
    drawFuel.dispose();
    drawAsteroid.dispose();

    hud.dispose();
    background.getTexture().dispose();
  }

  private Vector3 getMouseCoordinates(int screenX, int screenY) {
    Vector3 vector = new Vector3(screenX, screenY, 0);
    Vector3 coordinates = viewport.unproject(vector);

    return coordinates;
  }

  private void handleKeyPress() {
    // Unselect player on ESC
    if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
      playerSelected = false;
      pathRenderer.clearPath();
    }

    // Move camera with WASD
    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      if (checkCameraMove(0f, CAMERA_MOVE_SPEED))
        camera.translate(0f, CAMERA_MOVE_SPEED);
    }
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      if (checkCameraMove(-CAMERA_MOVE_SPEED, 0f))
        camera.translate(-CAMERA_MOVE_SPEED, 0f);
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      if (checkCameraMove(0f, -CAMERA_MOVE_SPEED))
        camera.translate(0f, -CAMERA_MOVE_SPEED);
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      if (checkCameraMove(0f, CAMERA_MOVE_SPEED))
        camera.translate(CAMERA_MOVE_SPEED, 0f);
    }
  }

  /**
   * Checks if the input x and y values would result in a valid camera movement
   * 
   * @param x movement in x direction
   * @param y movement in y direction
   * @return whether it's a valid camera move
   */
  private boolean checkCameraMove(float x, float y) {
    float cameraX = camera.position.x;
    float cameraY = camera.position.y;

    if (cameraX + x < 80 || cameraY + y < 300) {
      return false;
    } else if (cameraX + x > PPS / 0.03f || cameraY + y > (PPS - 10) / 0.025f) {
      return false;
    }
    return true;
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

    hud.touchDown(screenX, screenY, pointer, button);

    return false;
  }

  private boolean startMove(Vector3 mouse) {
    // Check a left click was performed
    if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT))
      return false;

    // Get player coordinates
    Player player = store.getMainPlayer();
    int playerX = player.getCoordinates().getX();
    int playerY = player.getCoordinates().getY();

    // Handle when mouse click is within player grid point
    if (playerX * PPS <= mouse.x && mouse.x <= (playerX + 1) * PPS && playerY * PPS <= mouse.y
        && mouse.y <= (playerY + 1) * PPS) {
      playerSelected = true;
      pathRenderer.clearPath();
    }

    return true;
  }

  private boolean endMove(Vector3 mouse) {
    // Check a left click was performed
    if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT))
      return false;

    // Check it's the client's turn to move
    if (!store.isMainPlayersTurn())
      return false;

    // Check if click is on the grid
    if (0 <= mouse.x && mouse.x <= gridSize * PPS && 0 <= mouse.y && mouse.y <= gridSize * PPS) {

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
    if (hud.isPaused())
      return false;

    // Mouse camera panning
    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
      float xDist = -(screenX - mouseDownX);
      float yDist = screenY - mouseDownY;

      if (checkCameraMove(xDist, yDist)) {
        camera.translate(xDist, yDist);
      }

      mouseDownX = screenX;
      mouseDownY = screenY;
      return true;
    }
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    // Check if player is selected
    if (!playerSelected)
      return false;

    // Get the board coordinates of where the mouse is positioned
    Vector3 mouse = getMouseCoordinates(screenX, screenY);
    Coordinates coordinates = new Coordinates((int) mouse.x / PPS, (int) mouse.y / PPS);

    // Do nothing if the mouse is in the same position as where the player currently is at
    if (coordinates.isEqual(store.getMainPlayer().getCoordinates()))
      return false;

    // Ensure the mouse is clicking on the board
    if (coordinates.getX() >= gridSize || coordinates.getX() < 0 || coordinates.getY() >= gridSize
        || coordinates.getY() < 0)
      return false;

    // Pathfind to where the mouse is located
    pathRenderer.generatePath(store.getMainPlayer().getCoordinates(), coordinates);
    return true;
  }

  @Override
  public boolean scrolled(int amount) {
    if (hud.isPaused())
      return false;

    // Zoom code
    if ((PPS -= amount) <= ((Config.GAME_WORLD_HEIGHT / gridSize) - 4)) {
      PPS = (Config.GAME_WORLD_HEIGHT / gridSize) - 3;
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
  private void showMinigamePrompt(UUID planetId) {
    Dialog dialog = new Dialog("", skin) {

      protected void result(Object object) {

        if (object.equals(true)) {
          SoundController.playSound("menuSelect");
          router.call(Route.SEND_MINIGAME_START, planetId);
        } else {
          SoundController.playSound("menuSelect");
          // Ends the turn
          router.call(Route.END_TURN);
        }
      }
    };

    dialog.text(new Label("Do you want to attempt to capture this planet?", skin, "window"));
    dialog.button("Yes", true);
    dialog.button("No", false);

    dialog.show(hud.getStage());
  }
}
