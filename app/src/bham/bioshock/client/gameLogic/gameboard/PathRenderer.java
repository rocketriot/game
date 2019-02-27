package bham.bioshock.client.gameLogic.gameboard;

import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

public class PathRenderer {
  private AStarPathfinding pathFinder;
  private ArrayList<Coordinates> path = new ArrayList<>();
  private ShapeRenderer sr;
  private Player mainPlayer;
  private OrthographicCamera camera;

  /** Specifies the current path goal to prevent useless rerenders */
  private Coordinates currentCoordinates;

  public PathRenderer(
      OrthographicCamera camera,
      GameBoard gameBoard,
      Player mainPlayer,
      ArrayList<Player> players) {
    pathFinder =
        new AStarPathfinding(
            gameBoard.getGrid(),
            mainPlayer.getCoordinates(),
            gameBoard.GRID_SIZE,
            gameBoard.GRID_SIZE,
            players);

    sr = new ShapeRenderer();

    this.camera = camera;
    this.mainPlayer = mainPlayer;
  }

  public void draw(int PPS) {
    if (path.size() == 0) return;

    sr.setProjectionMatrix(camera.combined);
    sr.begin(ShapeRenderer.ShapeType.Filled);

    Gdx.gl.glEnable(GL30.GL_BLEND);
    Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

    boolean[] allowedPath = getPathColour(mainPlayer.getFuel());

    // Draw white box at player position
    Coordinates coordinates = mainPlayer.getCoordinates();
    sr.setColor(255, 255, 255, 0.4f);
    sr.rect(PPS * coordinates.getX(), PPS * coordinates.getY(), PPS, PPS);

    // Draw Path
    for (int i = 1; i < path.size(); i++) {
      if (!allowedPath[i - 1]) {
        // Red
        sr.setColor(255, 0, 0, 0.5f);
      } else if (allowedPath[i - 1]) {
        // Green
        sr.setColor(124, 252, 0, 0.4f);
      }
      sr.rect(PPS * path.get(i).getX(), PPS * path.get(i).getY(), PPS, PPS);
    }

    sr.end();
    Gdx.gl.glDisable(GL30.GL_BLEND);
  }

  private boolean[] getPathColour(float fuel) {
    boolean[] allowedMove = new boolean[path.size()];

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

  public void generatePath(Coordinates start, Coordinates goal) {
    // Do nothing if generating the same path
    if (goal.isEqual(currentCoordinates)) return;

    pathFinder.setStartPosition(start);
    path = pathFinder.pathfind(goal);
  }

  public void clearPath() {
    currentCoordinates = new Coordinates(-1, -1);
    path.clear();
  }
}
