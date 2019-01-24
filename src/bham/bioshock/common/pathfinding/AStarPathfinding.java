package bham.bioshock.common.pathfinding;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;

import java.util.ArrayList;

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
        this.maxX = maxX;
        this.maxY = maxY;

        setGameGrid(grid);
    }

    // method to call to actually find the path
    public Coordinates[][] pathfind(Coordinates goalPosition) {
        this.goalPosition = goalPosition;

        // open list - list of all generated nodes
        ArrayList<Coordinates> openList = new ArrayList<>();
        // closed list - list of all expanded nodes
        ArrayList<Coordinates> closedList = new ArrayList<>();

        insertIntoList(openList, startPosition);

        while (!openList.isEmpty()) {
            Coordinates currentPosition = findMinPoint(openList); // get coordinates of point with smallest cost
            openList.remove(currentPosition);
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
        aStarGrid = setAStarGrid();
    }

    //method to setup the basics of the pathfindingValues grid
    private PathfindingValues[][] setAStarGrid() {

        PathfindingValues[][] tempGrid = new PathfindingValues[maxX][maxY]; // temporary grid to return

        // go through the passed gameGrid and assign values accordingly to the temporary grid
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                if (gameGrid[x][y].getType().equals("FUEL") || gameGrid[x][y].getType().equals("EMPTY")) {
                    tempGrid[x][y] = new PathfindingValues(0, 0, x, y, true);
                } else {
                    tempGrid[x][y] = new PathfindingValues(0, 0, x, y, false);
                }
            }
        }
        return tempGrid;
    }

    // insert something into the open/closed lists
    private void insertIntoList(ArrayList<Coordinates> list, Coordinates coords) {
        list.add(coords);
    }

    // method to find the point on the grid that has the minimum value in a list
    private Coordinates findMinPoint(ArrayList<Coordinates> list) {

        int minimumCost = Integer.MAX_VALUE;
        Coordinates nextPoint = new Coordinates(Integer.MAX_VALUE, Integer.MAX_VALUE);

        // iterate through the list to get an entrySet
        for (Coordinates currentCoords : list) {
            PathfindingValues value = aStarGrid[currentCoords.getX()][currentCoords.getY()];

            // check if the point can be traversed to
            if (value.isPassable() || currentCoords == startPosition) {
                // check if the current point has a lower cost than the current lowest cost point
                if (value.getTotalCost() < minimumCost) {
                    minimumCost = value.getTotalCost();
                    nextPoint = currentCoords;
                }
            }
        }
        return nextPoint;
    }

    // method to find the heuristic values for a node using Manhattan Distance
    private int findHeuristic(Coordinates position) {
        return (Math.abs(goalPosition.getX() - position.getX()) + Math.abs(goalPosition.getY() - position.getY()));
    }

    // method to generate the successors of the current point
    private ArrayList<Coordinates> generateSuccessors(Coordinates currentPoint){
        return null;
    }

}