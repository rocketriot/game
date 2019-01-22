package bham.bioshock.common.pathfinding;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;

import java.util.ArrayList;
import java.util.HashMap;

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

    // open list - list of all generated nodes
    private HashMap<Coordinates, PathfindingValues> openList;

    // closed list - list of all expanded nodes
    private HashMap<Coordinates, PathfindingValues> closedList;

    public AStarPathfinding(GridPoint[][] grid, Coordinates startPosition, Coordinates goalPosition) {
        this.startPosition = startPosition;
        currentPosition = startPosition;
        this.goalPosition = goalPosition;
        gameGrid = grid;
    }


    // method to set the start position
    public void setStartPosition(Coordinates startPosition) {
        this.startPosition = startPosition;
        currentPosition = startPosition;
    }

    // method to set the goal position
    public void setGoalPosition(Coordinates goalPosition) {
        this.goalPosition = goalPosition;
    }

    //method to set the game grid
    public void setGameGrid(GridPoint[][] grid) {
        gameGrid = grid;
    }

}