package bham.bioshock;

import bham.bioshock.common.consts.*;
import bham.bioshock.common.models.*;
import bham.bioshock.common.pathfinding.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PathfindingTests {

    private GridPoint[][] emptyGrid = new GridPoint[36][36];
    private Coordinates startPosition = new Coordinates(18, 18);
    private AStarPathfinding pathfinder;

    @BeforeAll
    public void setupTests(){
        //set up the empty grid
        for (int x = 0; x < 36; x++){
            for (int y = 0; y < 36; y++){
                GridPoint point = new GridPoint(GridPoint.Type.EMPTY, 0);
                emptyGrid[x][y] = point;
            }
        }
        pathfinder = new AStarPathfinding(emptyGrid, startPosition, 36, 36);
    }

    @Test
    public void leftTest() {

        Coordinates goalPosition = new Coordinates(0, 18);

        // set up the actual path that should be found
        ArrayList<Coordinates> truePath = new ArrayList<>();
        for (int i = 18; i >= 0; i--){
            Coordinates currentCoord = new Coordinates(i, 18);
            truePath.add(currentCoord);
        }

        ArrayList<Coordinates> foundPath = pathfinder.pathfind(goalPosition);

        for (int i = 0; i < truePath.size(); i++){
            Coordinates truePoint = truePath.get(i);
            Coordinates foundPoint = foundPath.get(i);
            System.out.println("True Path Coord: (" + truePoint.getX() + ", " + truePoint.getY() + ")");
            System.out.println("Found Path Coord: (" + foundPoint.getX() + ", " + foundPoint.getY() + ")");
        }

        assertTrue(truePath.equals(foundPath));
    }

//    @Test
//    public void rightTest() {
//
//    }
//
//    @Test
//    public void upTest() {
//
//    }
//
//    @Test
//    public void downTest() {
//
//    }
//
//    @Test
//    public void downLeftTest() {
//
//    }
//
//    @Test
//    public void downRightTest() {
//
//    }
//
//    @Test
//    public void upLeftTest() {
//
//    }
//
//    @Test
//    public void upRightTest() {
//
//    }
//
//    @Test
//    public void diagObstacleTest(){
//
//    }
//
//    @Test
//    public void linearObstacleTest(){
//
//    }
//
//    @Test
//    public void noPathTest(){
//
//    }

}
