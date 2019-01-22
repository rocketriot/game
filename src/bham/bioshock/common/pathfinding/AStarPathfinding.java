package bham.bioshock.common.pathfinding;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;

// method to find a path between two points
public class AStarPathfinding {

    // start position of the path
    private Coordinates startPosition;

    // goal position of the path
    private Coordinates goalPosition;

    // current position of the path
    private Coordinates currentPosition;

    // the game grid at the time of the pathfinding search
    private GridPoint[][] gameGrid;



    public AStarPathfinding(GridPoint[][] grid, Coordinates startPosition, Coordinates goalPosition){
        this.startPosition = startPosition;
        currentPosition = startPosition;
        this.goalPosition = goalPosition;
        gameGrid = grid;
    }

}
