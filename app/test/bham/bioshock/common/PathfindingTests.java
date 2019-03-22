package bham.bioshock.common;

import bham.bioshock.common.consts.*;
import bham.bioshock.common.models.*;
import bham.bioshock.common.pathfinding.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The pathfinding tests.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PathfindingTests {

  private GridPoint[][] emptyGrid = new GridPoint[36][36];
  private Coordinates startPosition = new Coordinates(18, 18);
  private AStarPathfinding pathfinder;

  /**
   * Set up the variables needed for the tests.
   */
  @BeforeAll
  public void setupTests() {
    //set up the empty grid
    for (int x = 0; x < 36; x++) {
      for (int y = 0; y < 36; y++) {
        GridPoint point = new GridPoint(GridPoint.Type.EMPTY, 0);
        emptyGrid[x][y] = point;
      }
    }
    pathfinder = new AStarPathfinding(emptyGrid, startPosition, 36, 36, new ArrayList<Player>());
  }

  /**
   * Left movement test.
   */
  @Test
  public void leftTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(0, 18);

    // set up the actual path that should be found
    ArrayList<Coordinates> truePath = new ArrayList<>();
    for (int i = 18; i >= 0; i--) {
      Coordinates currentCoord = new Coordinates(i, 18);
      truePath.add(currentCoord);
    }

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);
    assertTrue(checkPaths(truePath, foundPath));
  }

  /**
   * Right movement test.
   */
  @Test
  public void rightTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(35, 18);

    // set up the actual path that should be found
    ArrayList<Coordinates> truePath = new ArrayList<>();
    for (int i = 18; i < 36; i++) {
      Coordinates currentCoord = new Coordinates(i, 18);
      truePath.add(currentCoord);
    }

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);
    assertTrue(checkPaths(truePath, foundPath));
  }

  /**
   * Up movement test.
   */
  @Test
  public void upTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(18, 35);

    // set up the actual path that should be found
    ArrayList<Coordinates> truePath = new ArrayList<>();
    for (int i = 18; i < 36; i++) {
      Coordinates currentCoord = new Coordinates(18, i);
      truePath.add(currentCoord);
    }

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);
    assertTrue(checkPaths(truePath, foundPath));
  }

  /**
   * Down movement test.
   */
  @Test
  public void downTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(18, 0);

    // set up the actual path that should be found
    ArrayList<Coordinates> truePath = new ArrayList<>();
    for (int i = 18; i >= 0; i--) {
      Coordinates currentCoord = new Coordinates(18, i);
      truePath.add(currentCoord);
    }

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);
    assertTrue(checkPaths(truePath, foundPath));
  }

  /**
   * Down left movement test.
   */
  @Test
  public void downLeftTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(0, 0);

    // set up the actual path that should be found
    ArrayList<Coordinates> truePath = new ArrayList<>();
    for (int i = 18; i >= 0; i--) {
      Coordinates currentCoord = new Coordinates(18, i);
      truePath.add(currentCoord);
    }
    for (int i = 17; i >= 0; i--) {
      Coordinates currentCoord = new Coordinates(i, 0);
      truePath.add(currentCoord);
    }

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);
    assertTrue(checkPaths(truePath, foundPath));
  }

  /**
   * Down right movement test.
   */
  @Test
  public void downRightTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(35, 0);

    // set up the actual path that should be found
    ArrayList<Coordinates> truePath = new ArrayList<>();
    for (int i = 18; i >= 0; i--) {
      Coordinates currentCoord = new Coordinates(18, i);
      truePath.add(currentCoord);
    }
    for (int i = 19; i < 36; i++) {
      Coordinates currentCoord = new Coordinates(i, 0);
      truePath.add(currentCoord);
    }

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);
    assertTrue(checkPaths(truePath, foundPath));
  }

  /**
   * Up left movement test.
   */
  @Test
  public void upLeftTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(0, 35);

    // set up the actual path that should be found
    ArrayList<Coordinates> truePath = new ArrayList<>();
    for (int i = 18; i < 36; i++) {
      Coordinates currentCoord = new Coordinates(18, i);
      truePath.add(currentCoord);
    }
    for (int i = 17; i >= 0; i--) {
      Coordinates currentCoord = new Coordinates(i, 35);
      truePath.add(currentCoord);
    }

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);
    assertTrue(checkPaths(truePath, foundPath));
  }

  /**
   * Up right movement test.
   */
  @Test
  public void upRightTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(35, 35);

    // set up the actual path that should be found
    ArrayList<Coordinates> truePath = new ArrayList<>();
    for (int i = 18; i < 36; i++) {
      Coordinates currentCoord = new Coordinates(18, i);
      truePath.add(currentCoord);
    }
    for (int i = 19; i < 36; i++) {
      Coordinates currentCoord = new Coordinates(i, 35);
      truePath.add(currentCoord);
    }

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);
    assertTrue(checkPaths(truePath, foundPath));
  }

  /**
   * Diagonal obstacle movement test.
   */
  @Test
  public void diagObstacleTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(0, 0);

    // set up the obstacle
    emptyGrid[18][8].setType(GridPoint.Type.PLANET);
    pathfinder.setGameGrid(emptyGrid, new ArrayList<>());

    // set up the actual path that should be found
    ArrayList<Coordinates> truePath = new ArrayList<>();
    for (int i = 18; i >= 9; i--) {
      Coordinates currentCoord = new Coordinates(18, i);
      truePath.add(currentCoord);
    }
    for (int i = 9; i >= 0; i--) {
      Coordinates currentCoord = new Coordinates(17, i);
      truePath.add(currentCoord);
    }
    for (int i = 16; i >= 0; i--) {
      Coordinates currentCoord = new Coordinates(i, 0);
      truePath.add(currentCoord);
    }

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);
    assertTrue(checkPaths(truePath, foundPath));

    // remove the obstacle
    emptyGrid[18][8].setType(GridPoint.Type.EMPTY);
    pathfinder.setGameGrid(emptyGrid, new ArrayList<>());
  }

  /**
   * Linear obstacle movement test.
   */
  @Test
  public void linearObstacleTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(0, 18);

    // set up the obstacle
    emptyGrid[9][18].setType(GridPoint.Type.PLANET);
    pathfinder.setGameGrid(emptyGrid, new ArrayList<>());

    // set up the actual path that should be found
    ArrayList<Coordinates> truePath = new ArrayList<>();
    truePath.add(new Coordinates(18, 18));
    for (int i = 18; i >= 8; i--) {
      Coordinates currentCoord = new Coordinates(i, 19);
      truePath.add(currentCoord);
    }
    for (int i = 8; i >= 0; i--) {
      Coordinates currentCoord = new Coordinates(i, 18);
      truePath.add(currentCoord);
    }

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);

    // remove the obstacle
    emptyGrid[9][18].setType(GridPoint.Type.EMPTY);
    pathfinder.setGameGrid(emptyGrid, new ArrayList<>());

    assertTrue(checkPaths(truePath, foundPath));
  }

  /**
   * No path test.
   */
  @Test
  public void noPathTest() {
    Coordinates startPosition = new Coordinates(18, 18);
    pathfinder.setStartPosition(startPosition);
    Coordinates goalPosition = new Coordinates(0, 18);

    // set up the wall to stop any path
    for (int i = 0; i < 36; i++) {
      emptyGrid[9][i].setType(GridPoint.Type.PLANET);
    }
    pathfinder.setGameGrid(emptyGrid, new ArrayList<>());

    ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);

    // remove the wall
    for (int i = 0; i < 36; i++) {
      emptyGrid[9][i].setType(GridPoint.Type.EMPTY);
    }
    pathfinder.setGameGrid(emptyGrid, new ArrayList<>());

    assertTrue(foundPath.isEmpty());
  }

  /**
   * Method to compare two arraylists of coordinates because the default methods don't work
   *
   * @param truth the true path
   * @param found the path generated by the pathfinding algorithm
   * @return whether the two paths are equal
   */
  private static boolean checkPaths(ArrayList<Coordinates> truth, ArrayList<Coordinates> found) {
    if (!found.isEmpty() && truth.size() == found.size()) {
      for (int i = 0; i < truth.size(); i++) {
        if (!truth.get(i).isEqual(found.get(i))) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }
}