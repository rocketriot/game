package bham.bioshock;

import bham.bioshock.common.consts.*;
import bham.bioshock.common.models.*;
import bham.bioshock.common.pathfinding.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathfindingTests {

    GameBoard testBoard = new GameBoard(null);
    Coordinates startPosition = new Coordinates(18, 18);
    AStarPathfinding pathfinder = new AStarPathfinding(testBoard.getGrid(), startPosition, 36, 36);

    @Test
    public void leftTest() {
        

        Coordinates goalPosition = new Coordinates(0, 18);
        pathfinder.setGoalPosition(goalPosition);
    }

    @Test
    public void rightTest() {

    }

    @Test
    public void upTest() {

    }

    @Test
    public void downTest() {

    }

    @Test
    public void downLeftTest() {

    }

    @Test
    public void downRightTest() {

    }

    @Test
    public void upLeftTest() {

    }

    @Test
    public void upRightTest() {

    }

    @Test
    public void diagObstacleTest(){

    }

    @Test
    public void linearObstacleTest(){

    }

    @Test
    public void noPathTest(){

    }

}
