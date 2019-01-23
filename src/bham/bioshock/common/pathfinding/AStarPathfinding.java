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

    // the game grid at the time of the pathfinding search
    private GridPoint[][] gameGrid;

    // the grid that will contain the pathfinding values for each point
    private PathfindingValues[][] aStarGrid;

    // the maximum x value of the map
    private int maxX;

    // the maximum y value of the map
    private int maxY;

    // instantiation method
    public AStarPathfinding(GridPoint[][] grid, Coordinates startPosition, int maxX, int maxY) {
        this.startPosition = startPosition;
        gameGrid = grid;
        this.maxX = maxX;
        this.maxY = maxY;

        setAStarGrid();
    }

    // method to call to actually find the path
    public Coordinates[][] pathfind(Coordinates goalPosition) {
        this.goalPosition = goalPosition;

        // open list - list of all generated nodes
        HashMap<Coordinates, PathfindingValues> openList = new HashMap<>();
        // closed list - list of all expanded nodes
        HashMap<Coordinates, PathfindingValues> closedList = new HashMap<>();

        insertIntoHashmap(openList, startPosition);

        while (!openList.isEmpty()){

        }

        return null;
    }


    // method to set the start position
    public void setStartPosition(Coordinates startPosition) {
        this.startPosition = startPosition;
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

    //method to setup the basics of the pathfindingValues grid
    private void setAStarGrid() {

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

    // insert something into the open/closed hashmap
    private void insertIntoHashmap(HashMap<Coordinates, PathfindingValues> list, Coordinates coords) {
        list.put(startPosition, aStarGrid[coords.getX()][coords.getY()]);
    }

    // method to find the point on the grid that has the minimum value in a hashmap
    private Coordinates findMinPoint(HashMap<Coordinates, PathfindingValues> map, Coordinates currentPosition) {

        int minimumCost = Integer.MAX_VALUE;
        Coordinates nextPoint = new Coordinates(currentPosition.getX(), currentPosition.getY());

        // iterate through the hashmap to get an entrySet
        for (HashMap.Entry<Coordinates, PathfindingValues> entry : map.entrySet()) {
            Coordinates key = entry.getKey();
            PathfindingValues value = entry.getValue();

            // check if the current point has a lower cost than the current lowest cost point
            if (value.getTotalCost() < minimumCost) {
                minimumCost = value.getTotalCost();
                nextPoint = key;
            }
        }

        return nextPoint;
    }
}