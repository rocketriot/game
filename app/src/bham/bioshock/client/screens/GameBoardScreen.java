package bham.bioshock.client.screens;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.Direction;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class GameBoardScreen extends ScreenMaster implements InputProcessor {
  private final InputMultiplexer inputMultiplexer;
  private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
  private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
  /** The game data */
  private Store store;
  /** Pathfinding for player movement */
  private AStarPathfinding pathFinder;

  private ArrayList<Coordinates> path = new ArrayList<>();
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
  private int PPS = 27;

  /** Size of the board */
  private int gridSize;

  private Hud hud;
  private int mouseDownX, mouseDownY;
  private boolean playerSelected = false;
  private Coordinates oldGridCoords = new Coordinates(-1, -1);
  private Sprite movingSprite;
  private Array<ParticleEffect> effects = new Array<>();
  private ParticleEffect rocketTrail;
  private boolean drawRocketTrail;
  private boolean minigamePromptShown = false;
  private float msXCoords, msYCoords, rtXCoords, rtYCoords;
  private int boardMovePointer = 0;

  public GameBoardScreen(Router router, Store store) {
    super(router);

    this.store = store;

    this.batch = new SpriteBatch();
    this.sh = new ShapeRenderer();

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
    this.movingSprite = new Sprite();

    generateEffects();

    setupUI();

    // Setup the input processing
    this.inputMultiplexer = new InputMultiplexer();
    this.inputMultiplexer.addProcessor(hud.getStage());
    this.inputMultiplexer.addProcessor(this);
  }

  private void generateEffects() {
    rocketTrail = new ParticleEffect();
    rocketTrail.load(
        Gdx.files.internal("app/assets/particle-effects/rocket-trail.p"),
        Gdx.files.internal("app/assets/particle-effects"));
    rocketTrail.start();
    effects.add(rocketTrail);
  }

  private void setupUI() {
    hud = new Hud(batch, skin, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, store, router);
    background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));
  }

  /** Draws the player move */
  private void drawPlayerMove(Player player) {
    GameBoard gameBoard = store.getGameBoard();
    BoardMove boardMove = player.getBoardMove();
    if (boardMove.getDirections().size() == boardMovePointer) {

      if (gameBoard.isNextToThePlanet(player.getCoordinates()) && !minigamePromptShown) {
        this.minigamePromptShown = true;
        showMinigamePrompt();
      }

      this.drawRocketTrail = false;
      player.setBoardMove(null);
      boardMovePointer = 0;
      if (player.getId().equals(store.getMainPlayer().getId())) {
        playerSelected = true;
        pathFinder.setStartPosition(player.getCoordinates());
      }
    } else {
      // Calculate distance to travel
      float distanceToMove = 3 * Gdx.graphics.getDeltaTime();

      movingSprite = playerSprites.get(player.getTextureID());
      movingSprite.setOriginCenter();

      // Only true for first call of each move
      if (boardMovePointer == 0) {
        // Flag for renderer to draw rocket trail particle effects
        this.drawRocketTrail = true;

        if (player.equals(store.getMainPlayer())) {
          // Stops the renderer drawing the old path
          playerSelected = false;
          path = null;
        }

        // Coordinates of the moving sprite
        msXCoords = boardMove.getStartCoords().getX();
        msYCoords = boardMove.getStartCoords().getY();

        boardMovePointer += 1;
      }
      switch (boardMove.getDirections().get(boardMovePointer)) {
        case UP:
          movingSprite.setRotation(0);
          msYCoords += distanceToMove;

          // Rocket trail coordinates
          rtXCoords = msXCoords + 0.5f;
          rtYCoords = msYCoords;
          // Rocket trail rotation
          setEmmiterAngle(rocketTrail, 0);

          // Has the sprite reach the next coordinate in the board move
          if (movingSprite.getY() >= boardMove.getPosition().get(boardMovePointer).getY() * PPS) {
            movingSprite.setY(boardMove.getPosition().get(boardMovePointer).getY() * PPS);
            boardMovePointer += 1;
          }
          break;
        case DOWN:
          movingSprite.setRotation(180);
          msYCoords -= distanceToMove;

          rtXCoords = msXCoords + 0.5f;
          rtYCoords = msYCoords + 1;
          setEmmiterAngle(rocketTrail, 180);
          if (movingSprite.getY() <= boardMove.getPosition().get(boardMovePointer).getY() * PPS) {
            movingSprite.setY(boardMove.getPosition().get(boardMovePointer).getY() * PPS);
            boardMovePointer += 1;
          }
          break;
        case RIGHT:
          movingSprite.setRotation(270);
          msXCoords += distanceToMove;

          rtXCoords = msXCoords;
          rtYCoords = msYCoords + 0.5f;
          setEmmiterAngle(rocketTrail, 270);
          if (movingSprite.getX() >= boardMove.getPosition().get(boardMovePointer).getX() * PPS) {
            movingSprite.setX(boardMove.getPosition().get(boardMovePointer).getX() * PPS);
            boardMovePointer += 1;
          }
          break;
        case LEFT:
          movingSprite.setRotation(90);
          msXCoords -= distanceToMove;

          rtXCoords = msXCoords + 1;
          rtYCoords = msYCoords + 0.5f;
          setEmmiterAngle(rocketTrail, 90);
          if (movingSprite.getX() <= boardMove.getPosition().get(boardMovePointer).getX() * PPS) {
            movingSprite.setX(boardMove.getPosition().get(boardMovePointer).getX() * PPS);
            boardMovePointer += 1;
          }
          break;
      }
      rocketTrail.setPosition(rtXCoords * PPS, rtYCoords * PPS);
      movingSprite.setX(msXCoords * PPS);
      movingSprite.setY(msYCoords * PPS);
      movingSprite.draw(batch);
    }
  }

  private void setEmmiterAngle(ParticleEffect particleEffect, float angle) {
    // Align particle effect angle with world
    angle -= 90;
    for (ParticleEmitter pe : particleEffect.getEmitters()) {
      ParticleEmitter.ScaledNumericValue val = pe.getAngle();
      float amplitude = (val.getHighMax() - val.getHighMin()) / 2f;
      float h1 = angle + amplitude;
      float h2 = angle - amplitude;
      val.setHigh(h1, h2);
      val.setLow(angle);
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
            //TODO REMOVE DEBUG CODE
            planet.setPlayerCaptured(store.getMainPlayer());

            if (!store.getPlanets().contains(planet)) {
              store.addPlanets(planet);
            }
            // Check if planet has already been drawn
            if (!planet.getCoordinates().isEqual(new Coordinates(x, y))) continue;

            sprite = planetSprites.get(planet.getTextureID());
            break;

          case ASTEROID:
            Asteroid asteroid = (Asteroid) grid[x][y].getValue();

            // Check if asteroid has already been drawn
            if (!asteroid.getCoordinates().isEqual(new Coordinates(x, y))) continue;

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
  }

  private void drawPlayers() {
    boolean drawnMove = false;
    // Draw players
    for (Player player : store.getPlayers()) {
      // Checks if the player's move needs to be drawn
      if (player.getBoardMove() != null && !drawnMove) {
        drawPlayerMove(player);
        drawnMove = true;
      } else if (player.getBoardMove() == null) {
        if (playerSelected && player.equals(store.getMainPlayer())) {
          sprite = outlinedPlayerSprites.get(player.getTextureID());
        } else {
          sprite = playerSprites.get(player.getTextureID());
        }
        sprite.setX(player.getCoordinates().getX() * PPS);
        sprite.setY(player.getCoordinates().getY() * PPS);
        sprite.draw(batch);
      }
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
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    return new Sprite(texture);
  }

  private void drawGridLines() {
    sh.setProjectionMatrix(camera.combined);
    sh.begin(ShapeRenderer.ShapeType.Line);
    Gdx.gl.glEnable(GL30.GL_BLEND);
    Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    sh.setColor(211, 211, 211, 0.2f);
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
    pathFinder =
        new AStarPathfinding(
            store.getGameBoard().getGrid(),
            store.getMainPlayer().getCoordinates(),
            gridSize,
            gridSize,
            store.getPlayers());
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
      s.setSize(PPS, PPS);
    }
    for (Sprite s : outlinedPlayerSprites) {
      s.setSize(PPS, PPS);
    }
    fuelSprite.setSize(PPS, PPS);
    movingSprite.setSize(PPS, PPS);
    background.setSize(PPS * 38.4f, PPS * 21.6f);
  }

  @Override
  public void render(float delta) {
    batch.setProjectionMatrix(camera.combined);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    handleInput();
    camera.update();

    batch.begin();
    drawBackground();
    batch.end();

    // Needs to be drawn in front of the background but behind the board objects
    drawPlanetOwnerShip();

    batch.begin();
    // Batch drawn methods
    drawBoardObjects();
    drawPlayers();
    drawEffects(delta);
    batch.end();

    // Shape render drawn methods
    drawPath();
    drawGridLines();

    // Draw the ui
    this.batch.setProjectionMatrix(hud.stage.getCamera().combined);
    hud.getStage().act(Gdx.graphics.getDeltaTime());
    hud.updateHud();
    hud.getStage().draw();
  }

  private void drawEffects(float delta) {
    if (drawRocketTrail) {
      for (ParticleEffect e : effects) {
        e.draw(batch);
        e.update(delta);
      }
    }
  }

  private void drawPlanetOwnerShip() {
    sh.begin(ShapeType.Filled);
    Gdx.gl.glEnable(GL30.GL_BLEND);
    Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    sh.setColor(255/255f, 150/255f, 60/255f, 0.5f);
    for (Planet p : store.getPlanets()) {
      Player player = p.getPlayerCaptured();
      Coordinates planetCoords = p.getCoordinates();
      sh.rect(planetCoords.getX() * PPS, planetCoords.getY() * PPS, PPS * 3, PPS * 3);
    }
    sh.end();
    Gdx.gl.glDisable(GL30.GL_BLEND);
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
    if (playerSelected && path != null) {
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
        if (!allowedPath[i - 1]) {
          // Red
          sh.setColor(255, 0, 0, 0.5f);
        } else if (allowedPath[i - 1]) {
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
    for (ParticleEffect e : effects) {
      e.dispose();
    }
  }

  private Vector3 getWorldCoords(int screenX, int screenY) {
    Vector3 coords = new Vector3(screenX, screenY, 0);
    coords = viewport.unproject(coords);
    return coords;
  }

  private void handleInput() {
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
    Player player = store.getMainPlayer();
    Player movingPlayer = store.getMovingPlayer();
    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
      // Used for mouse panning
      mouseDownX = screenX;
      mouseDownY = screenY;

      if (clickCoords.x >= player.getCoordinates().getX() * PPS
          && clickCoords.x <= (player.getCoordinates().getX() + 1) * PPS) {
        if (clickCoords.y >= player.getCoordinates().getY() * PPS
            && clickCoords.y <= (player.getCoordinates().getY() + 1) * PPS) {
          playerSelected = true;
          path = null;
        }
      }
      return true;
    } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {

      // Do nothing if it's not the client's turn to move
      if (!movingPlayer.getId().equals(player.getId())) return true;

      this.minigamePromptShown = false;
      // Move ship to click position
      Coordinates gridCoords =
          new Coordinates((int) clickCoords.x / PPS, (int) clickCoords.y / PPS);

      if (!store.getMainPlayer().getCoordinates().isEqual(gridCoords)) {
        router.call(Route.MOVE_PLAYER, gridCoords);
        return true;
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
    if (playerSelected) {
      Vector3 mouseCoords = getWorldCoords(screenX, screenY);
      Coordinates gridCoords =
          new Coordinates((int) mouseCoords.x / PPS, (int) mouseCoords.y / PPS);
      if (!oldGridCoords.isEqual(gridCoords)) {
        if (gridCoords.getX() < gridSize && gridCoords.getX() >= 0) {
          if (gridCoords.getY() < gridSize && gridCoords.getY() >= 0) {
            if (!gridCoords.isEqual(store.getMainPlayer().getCoordinates())) {
              path = pathFinder.pathfind(gridCoords);
              oldGridCoords = gridCoords;
              return true;
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
    Dialog diag =
        new Dialog("Start Minigame", skin) {

          protected void result(Object object) {

            if (object.equals(true)) {
              System.out.println("Starting minigame");
              router.call(Route.START_MINIGAME);
            } else {
              System.out.println("Minigame not started");
            }
          }
        };

    diag.text(new Label("Would you like to start a minigame to take over the planet?", skin));
    diag.button("Yes", true);
    diag.button("No", false);

    diag.show(hud.getStage());
  }
}
