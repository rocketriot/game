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

    // the grid that will contain the pathfinding values for each point
    private PathfindingValues[][] aStarGrid;

    // open list - list of all generated nodes
    private HashMap<Coordinates, PathfindingValues> openList;

    // closed list - list of all expanded nodes
    private HashMap<Coordinates, PathfindingValues> closedList;

    // the maximum x value of the map
    private int maxX;

    // the maximum y value of the map
    private int maxY;

    public AStarPathfinding(GridPoint[][] grid, Coordinates startPosition, Coordinates goalPosition, int maxX, int maxY) {
        this.startPosition = startPosition;
        currentPosition = startPosition;
        this.goalPosition = goalPosition;
        gameGrid = grid;
        this.maxX = maxX;
        this.maxY = maxY;

        setAStarGrid();
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
        setAStarGrid();
    }

    //method to setup the basics of the aStar grid
    public void setAStarGrid() {

        // go through the passed gameGrid and assign values accordingly to the aStarGrid
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                if (gameGrid[x][y].getType().equals("FUEL") || gameGrid[x][y].getType().equals("EMPTY")) {
                    aStarGrid[x][y] = new PathfindingValues(0, 0, x, y, true);
                } else {
                    aStarGrid[x][y] = new PathfindingValues(0, 0, x, y, false);
                }
            }
        }
    }

}