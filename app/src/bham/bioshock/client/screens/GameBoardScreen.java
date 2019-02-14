package bham.bioshock.client.screens;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.GameBoardController;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.Direction;
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
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class GameBoardScreen extends ScreenMaster implements InputProcessor {
  private final InputMultiplexer inputMultiplexer;
  private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
  private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;

  private GameBoard gameBoard;
  private Store store;
  private AStarPathfinding pathFinder;

  private SpriteBatch batch;
  private Sprite background;
  private OrthographicCamera camera;
  private FitViewport viewport;
  private ShapeRenderer sh;
  private ArrayList<Sprite> planetSprites;
  private ArrayList<Sprite> asteroidSprites;
  private ArrayList<Sprite> playerSprites;
  private int PPS;
  private int gridSize;
  private Hud hud;
  private int mouseDownX, mouseDownY;
  private boolean playerSelected = false;
  private ArrayList<Sprite> outlinedPlayerSprites;
  private ArrayList<Coordinates> path = new ArrayList<>();
  private Coordinates oldGridCoords = new Coordinates(-1, -1);
  private Sprite movingSprite;
  private Array<ParticleEffect> effects = new Array<>();
  private ParticleEffect rocketTrail;
  private boolean drawRocketTrail;
  private float msXCoords, msYCoords, rtXCoords, rtYCoords;

  public GameBoardScreen(Router router, Store store, GameBoard gameBoard) {
    super(router);

    this.gameBoard = gameBoard;
    this.store = store;

    batch = new SpriteBatch();

    // Pixels Per Square (on the grid)
    PPS = 50;

    this.gridSize = store.getGameBoard().GRID_SIZE;
    camera = new OrthographicCamera();
    viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, camera);
    viewport.apply();

    // Generate the arraylists of sprites
    genAsteroidSprites();
    genPlanetSprites();
    genPlayerSprites();
    genEffects();

    setupUI();

    // Setup the input processing
    inputMultiplexer = new InputMultiplexer();
    inputMultiplexer.addProcessor(hud.getStage());
    inputMultiplexer.addProcessor(this);

    sh = new ShapeRenderer();
  }

  private void genEffects() {
    rocketTrail = new ParticleEffect();
    rocketTrail.load(Gdx.files.internal("app/assets/particle-effects/rocket-trail.p"),
        Gdx.files.internal("app/assets/particle-effects"));
    rocketTrail.start();
    effects.add(rocketTrail);
  }

  private void setupUI() {
    hud = new Hud(batch, skin, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, store, router);
    background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));
  }

  /**
   * Draws the player move
   */
  private void drawPlayerMove(Player player) {
    BoardMove boardMove = player.getBoardMove();
    if (boardMove.getDirections().size() == 0) {
      this.drawRocketTrail = false;
      playerSelected = true;
      player.setBoardMove(null);
    } else {
      // Calculate distance to travel
      float distanceToMove = 3 * Gdx.graphics.getDeltaTime();

      movingSprite = playerSprites.get(player.getTextureID());
      movingSprite.setOriginCenter();

      // Only true for first call of each move
      if (boardMove.getDirections().get(0).equals(Direction.NONE)) {
        // Flag for renderer to draw rocket trail particle effects
        this.drawRocketTrail = true;

        // Stops the renderer drawing the old path
        playerSelected = false;
        path = null;

        msXCoords = boardMove.getStartCoords().getX();
        msYCoords = boardMove.getStartCoords().getY();

        boardMove.getPosition().remove(0);
        boardMove.getDirections().remove(0);
      }
      switch (boardMove.getDirections().get(0)) {
        case UP:
          movingSprite.setRotation(0);
          msYCoords += distanceToMove;

          rtXCoords = msXCoords + 0.5f;
          rtYCoords = msYCoords;
          setEmmiterAngle(rocketTrail, 0);
          if (movingSprite.getY() >= boardMove.getPosition().get(0).getY() * PPS) {
            movingSprite.setY(boardMove.getPosition().get(0).getY() * PPS);
            boardMove.getPosition().remove(0);
            boardMove.getDirections().remove(0);
          }
          break;
        case DOWN:
          movingSprite.setRotation(180);
          msYCoords -= distanceToMove;

          rtXCoords = msXCoords + 0.5f;
          rtYCoords = msYCoords + 1;
          setEmmiterAngle(rocketTrail, 180);
          if (movingSprite.getY() <= boardMove.getPosition().get(0).getY() * PPS) {
            movingSprite.setY(boardMove.getPosition().get(0).getY() * PPS);
            boardMove.getPosition().remove(0);
            boardMove.getDirections().remove(0);
          }
          break;
        case RIGHT:
          movingSprite.setRotation(270);
          msXCoords += distanceToMove;

          rtXCoords = msXCoords;
          rtYCoords = msYCoords + 0.5f;
          setEmmiterAngle(rocketTrail, 270);
          if (movingSprite.getX() >= boardMove.getPosition().get(0).getX() * PPS) {
            movingSprite.setX(boardMove.getPosition().get(0).getX() * PPS);
            boardMove.getPosition().remove(0);
            boardMove.getDirections().remove(0);
          }
          break;
        case LEFT:
          movingSprite.setRotation(90);
          msXCoords -= distanceToMove;

          rtXCoords = msXCoords + 1;
          rtYCoords = msYCoords + 0.5f;
          setEmmiterAngle(rocketTrail, 90);
          if (movingSprite.getX() <= boardMove.getPosition().get(0).getX() * PPS) {
            movingSprite.setX(boardMove.getPosition().get(0).getX() * PPS);
            boardMove.getPosition().remove(0);
            boardMove.getDirections().remove(0);
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

  private void drawBoardObjects() {
    GridPoint[][] grid = gameBoard.getGrid();
    boolean drawnMove = false;
    Sprite sprite;
    for (int x = 0; x < grid.length; x++) {
      for (int y = 0; y < grid[x].length; y++) {
        GridPoint.Type pType = grid[x][y].getType();
        if (pType == GridPoint.Type.PLAYER) {
          Player player = (Player) grid[x][y].getValue();

          //TODO remove code once player is sent/received by server
          player.setCoordinates(new Coordinates(x, y));
          if (player.getBoardMove() != null && !drawnMove) {
            drawPlayerMove(player);
            drawnMove = true;
          } else if (player.getBoardMove() == null) {
            if (playerSelected && player.equals(store.getMainPlayer())) {
              sprite = outlinedPlayerSprites.get(player.getTextureID());
            } else {
              sprite = playerSprites.get(player.getTextureID());
            }
            sprite.setX(x * PPS);
            sprite.setY(y * PPS);
            sprite.draw(batch);
          }
        } else if (pType == GridPoint.Type.PLANET) {
          Planet planet = (Planet) grid[x][y].getValue();
          if (!planet.isDrawn()) {
            planet.setDrawn(true);
            sprite = planetSprites.get(planet.getTextureID());
            sprite.setX(x * PPS);
            sprite.setY(y * PPS);
            sprite.draw(batch);
          }
        } else if (pType == GridPoint.Type.ASTEROID) {
          Asteroid asteroid = (Asteroid) grid[x][y].getValue();
          if (!asteroid.isDrawn()) {
            asteroid.setDrawn(true);
            sprite = asteroidSprites.get(asteroid.getTextureID());
            sprite.setX(x * PPS);
            sprite.setY(y * PPS);
            sprite.draw(batch);
          }
        }
      }
    }
    for (GridPoint[] gridPoints : grid) {
      for (GridPoint gridPoint : gridPoints) {
        GridPoint.Type pType = gridPoint.getType();
        if (pType == GridPoint.Type.PLANET) {
          Planet asteroid = (Planet) gridPoint.getValue();
          asteroid.setDrawn(false);
        } else if (pType == GridPoint.Type.ASTEROID) {
          Asteroid asteroid = (Asteroid) gridPoint.getValue();
          asteroid.setDrawn(false);
        }
      }
    }

    for (Player player : store.getPlayers()) {
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

  private void genPlanetSprites() {
    planetSprites = new ArrayList<>();
    FileHandle[] fh = Gdx.files.internal("app/assets/entities/planets").list();
    for (FileHandle f : fh) {
      Texture planetTexture = new Texture(Gdx.files.internal(f.path()));
      planetSprites.add(new Sprite(planetTexture));
    }
  }

  private void genAsteroidSprites() {
    asteroidSprites = new ArrayList<>();
    FileHandle[] fh = Gdx.files.internal("app/assets/entities/asteroids").list();
    for (FileHandle f : fh) {
      Texture asteroidTexture = new Texture(Gdx.files.internal(f.path()));
      asteroidSprites.add(new Sprite(asteroidTexture));
    }
  }

  private void genPlayerSprites() {
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
    movingSprite = new Sprite();
  }

  private void drawGridLines() {
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
    // Graphics.DisplayMode display = Gdx.graphics.getDisplayMode();
    // Gdx.graphics.setFullscreenMode(display);
    Gdx.input.setInputProcessor(inputMultiplexer);
    pathFinder = new AStarPathfinding(store.getGameBoard().getGrid(), store.getMainPlayer().getCoordinates(), gridSize, gridSize);
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

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
    movingSprite.setSize(PPS, PPS);
    background.setSize(PPS * 38.4f, PPS * 21.6f);
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
    drawEffects(delta);

    batch.end();

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
      // Move ship to click position
      Coordinates gridCoords = new Coordinates((int) clickCoords.x / PPS,
          (int) clickCoords.y / PPS);
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
      Coordinates gridCoords = new Coordinates((int) mouseCoords.x / PPS,
          (int) mouseCoords.y / PPS);
      if (!oldGridCoords.isEqual(gridCoords)) {
        if (gridCoords.getX() < gridSize - 1 && gridCoords.getX() >= 0) {
          if (gridCoords.getY() < gridSize - 1 && gridCoords.getY() >= 0) {
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
}
