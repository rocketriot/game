package bham.bioshock.common.pathfinding;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

// method to find a path between two points
public class AStarPathfinding {

    // the cost to go from one point to another
    private final int TRANSITION_COST = 1;

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
        setStartPosition(startPosition);
        this.maxX = maxX;
        this.maxY = maxY;

        setGameGrid(grid);
    }

    // method to call to actually find the path
    public ArrayList<Coordinates> pathfind(Coordinates goalPosition) {
        setGoalPosition(goalPosition);

        // open list - list of all generated nodes
        ArrayList<Coordinates> openList = new ArrayList<>();
        // closed list - list of all expanded nodes
        ArrayList<Coordinates> closedList = new ArrayList<>();

        // add the start position to the open list
        insertIntoList(openList, startPosition);

        // while there are points to check
        while (!openList.isEmpty()) {
            Coordinates currentPosition = findMinPoint(openList); // get coordinates of point with smallest cost
            openList.remove(currentPosition);
            closedList.add(currentPosition);
            ArrayList<Coordinates> successors = generateSuccessors(currentPosition, closedList);

            // iterate through the successors
            for (Coordinates currentSuccessor : successors) {
                int x = currentSuccessor.getX();
                int y = currentSuccessor.getY();
                // check if the goal has been found
                if (goalPosition.isEqual(currentSuccessor)) {
                    // if so: update the parent, get the path and return it
                    aStarGrid[goalPosition.getX()][goalPosition.getY()].setParent(currentPosition);
                    return getPath();
                } else { // otherwise, calculate pathfinding values
                    // calculate current path cost
                    int successorPathCost = aStarGrid[x][y].getPathCost() + TRANSITION_COST;
                    // calculate the heuristic cost
                    int successorHeuristicCost = findHeuristic(currentSuccessor);
                    // update the total cost value
                    int totalCost = successorPathCost + successorHeuristicCost;

                    // check if the successor is already in the open list
                    if (checkList(currentSuccessor, openList)) {
                        int openCost = aStarGrid[x][y].getTotalCost();
                        // check if the total cost calculated is less than the one stored for the point
                        if (openCost > totalCost) { // if so, update the values
                            updateValues(currentSuccessor, successorPathCost, successorHeuristicCost, currentPosition);
                        }
                        // else check if the node is already in the closed list
                    } else if (checkList(currentSuccessor, closedList)) {
                        int closedCost = aStarGrid[x][y].getTotalCost();
                        // check if the total cost calculated is less than the one stored for the point
                        if (closedCost > totalCost) { // if so, update the values and move point to open list
                            updateValues(currentSuccessor, successorPathCost, successorHeuristicCost, currentPosition);
                            closedList.remove(currentSuccessor);
                            openList.add(currentSuccessor);
                        }
                        // otherwise, update the values and add the node to the open list
                    } else {
                        updateValues(currentSuccessor, successorPathCost, successorHeuristicCost, currentPosition);
                        openList.add(currentSuccessor);
                    }
                }
            }
            // add the current node to the closed list
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

                if (gameGrid[x][y].getType() == GridPoint.Type.EMPTY || gameGrid[x][y].getType() == GridPoint.Type.FUEL) {
                    tempGrid[x][y] = new PathfindingValues(0, 0, null, true);
                } else {
                    tempGrid[x][y] = new PathfindingValues(0, 0, null, false);
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
            System.out.println(aStarGrid);
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
    private ArrayList<Coordinates> generateSuccessors(Coordinates currentPoint, ArrayList<Coordinates> closedList) {

        ArrayList<Coordinates> successors = new ArrayList<>();

        // check point directly above current point
        Coordinates upPoint = new Coordinates(currentPoint.getX(), currentPoint.getY() + 1);
        if (isValid(upPoint) && !checkList(upPoint, closedList)) {
            successors.add(upPoint);
        }

        // check point directly below currently point
        Coordinates downPoint = new Coordinates(currentPoint.getX(), currentPoint.getY() - 1);
        if (isValid(downPoint) && !checkList(downPoint, closedList)) {
            successors.add(downPoint);
        }

        // check point to the left of the current point
        Coordinates leftPoint = new Coordinates(currentPoint.getX() - 1, currentPoint.getY());
        if (isValid(leftPoint) && !checkList(leftPoint, closedList)) {
            successors.add(leftPoint);
        }

        // check point to the right of the current point
        Coordinates rightPoint = new Coordinates(currentPoint.getX() + 1, currentPoint.getY());
        if (isValid(rightPoint) && !checkList(rightPoint, closedList)) {
            successors.add(rightPoint);
        }

        return successors;
    }

    // method to check whether a coordinate is valid
    private Boolean isValid(Coordinates point) {
        int x = point.getX();
        int y = point.getY();
        if (x < maxX && x >= 0 && y < maxX && y >= 0 && aStarGrid[x][y].isPassable()) {
            return true;
        } else {
            return false;
        }
    }

    // method to check whether a coordinate is in one of the lists
    private Boolean checkList(Coordinates coordinate, ArrayList<Coordinates> list) {
        for (Coordinates listCoord : list) {
            if (listCoord.isEqual(coordinate)) {
                return true;
            }
        }
        return false;
    }

    // method to update the values of a point in the aStarGrid
    private void updateValues(Coordinates point, int pathCost, int heuristicCost, Coordinates parent) {
        aStarGrid[point.getX()][point.getY()].setPathCost(pathCost);
        aStarGrid[point.getX()][point.getY()].setHeuristicCost(heuristicCost);
        aStarGrid[point.getX()][point.getY()].updateTotalCost();
        aStarGrid[point.getX()][point.getY()].setParent(parent);
    }

    // method to get the found path from the end node to the start node
    private ArrayList<Coordinates> getPath() {
        ArrayList<Coordinates> path = new ArrayList<>();
        Coordinates currentPoint = goalPosition;

        // iterate through the path, finding the next node by getting the parent of the current node
        while (currentPoint != startPosition) {
            path.add(currentPoint);
            currentPoint = aStarGrid[currentPoint.getX()][currentPoint.getY()].getParent();
        }

        path.add(startPosition);
        Collections.reverse(path);
        return path;
    }

}