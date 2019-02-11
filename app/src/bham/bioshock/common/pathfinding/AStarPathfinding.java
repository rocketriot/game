package bham.bioshock.common.pathfinding;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.Coordinates;

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

    /*
     * Method to instantiate the AStarPathfinding object
     *
     * @param   grid           The grid from the game board to pathfind on
     * @param   startPosition  The position that the pathfinding needs to start from
     * @param   maxX           The maximum x value of the grid, i.e. the x limit of the game map
     * @param   maxY           The maximum y value of the grid, i.e. the y limit of the game map
     */
    public AStarPathfinding(GridPoint[][] grid, Coordinates startPosition, int maxX, int maxY) {
        setStartPosition(startPosition);
        this.maxX = maxX;
        this.maxY = maxY;

        setGameGrid(grid);
    }

    /*
     * Method to find the path between the start position and the inputted goal position
     *
     * @param   goalPosition    The position on the grid that the path needs to end at
     * @return                  The path that the algorithm has found to be the shortest
     */
    public ArrayList<Coordinates> pathfind(Coordinates goalPosition) {
        setGoalPosition(goalPosition);
        aStarGrid = setAStarGrid(); // set the A* Grid to be the current grid
        aStarGrid[startPosition.getX()][startPosition.getY()].setTotalCost(0);

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
                    double successorHeuristicCost = findHeuristic(currentSuccessor);
                    // update the total cost value
                    double totalCost = successorPathCost + successorHeuristicCost;

                    // check if the successor is already in the open list
                    if (checkList(currentSuccessor, openList)) {
                        double openCost = aStarGrid[x][y].getTotalCost();
                        // check if the total cost calculated is less than the one stored for the point
                        if (openCost > totalCost) { // if so, update the values
                            updateValues(currentSuccessor, successorPathCost, successorHeuristicCost, currentPosition);
                        }
                        // else check if the node is already in the closed list
                    } else if (checkList(currentSuccessor, closedList)) {
                        double closedCost = aStarGrid[x][y].getTotalCost();
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
        return new ArrayList<>();
    }

    /*
     * Method to set the start position of the pathfinding algorithm
     *
     * @param startPosition The position that the pathfinding needs to start from
     */
    public void setStartPosition(Coordinates startPosition) {
        this.startPosition = startPosition;
    }

    /*
     * Method to set the goal position of the pathfinding algorithm
     *
     * @param startPosition The position that the pathfinding needs to get to
     */
    public void setGoalPosition(Coordinates goalPosition) {
        this.goalPosition = goalPosition;
    }

    /*
     * Method to set the game grid that will be used for pathfinding
     *
     * @param grid The grid that will be used
     */
    public void setGameGrid(GridPoint[][] grid) {
        gameGrid = grid;
    }

    /*
     * Method to set the aStarGrid to the current grid. This grid stores the heuristic values
     * that the pathfinding algorithm uses to determine which point to go to next, as well as
     * whether each point is passable and the parent of each point.
     */
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

    /*
     * Method to insert a value into the required list - either the open or closed list
     *
     * @param list   The list to add to
     * @param coords The coordinates to add
     */
    private void insertIntoList(ArrayList<Coordinates> list, Coordinates coords) {
        list.add(coords);
    }

    /*
     * Method to find the point in a list that has the smallest heuristic value for expansion
     *
     * @param list The list that contains the coordinates to be searched through
     * @return     The Coordinate value of the point with the smallest heuristic value
     */
    private Coordinates findMinPoint(ArrayList<Coordinates> list) {

        double minimumCost = Integer.MAX_VALUE;
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
                } else if (value.getTotalCost() == minimumCost) {

                    minimumCost = value.getTotalCost();
                    nextPoint = currentCoords;
                }
            }
        }
        return nextPoint;
    }

    /*
     * Method to find the heuristic value for a given point - takes the cross product of the Manhattan distance
     * from the current point to the goal and from the start point to the goal and scales it so that it prefers
     * straight paths to the goal
     *
     * @param position The coordinates of the point you want to calculate the heuristic value for
     * @return         The heuristic value found
     */
    private double findHeuristic(Coordinates position) {
//       int dx1 = position.getX() - goalPosition.getX();
//       int dy1 = position.getY() - goalPosition.getY();
//       int dx2 = startPosition.getX() - goalPosition.getX();
//       int dy2 = startPosition.getY() - goalPosition.getY();
//       int cross = Math.abs(dx1*dy2 - dx2*dy1);
//       return (cross);
        return (Math.abs(position.getX() - goalPosition.getX()) + Math.abs(position.getY() - goalPosition.getY()));
    }

    /*
     * Method to generate the successors of a given point in all 4 directions, checking if they are valid first
     *
     * @param currentPoint The coordinates of the current point to generate the successors for
     * @param closedList   The closed list, containing all the already closed nodes that you do not want to become
     *                     successors to another node
     * @return             A list of the valid successors found
     */
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

    /*
     * Method to check whether the point is valid - is within the board coordinates and is passable
     *
     * @param point The coordinates of the point to check
     * @return             Whether the point is valid or not
     */
    private Boolean isValid(Coordinates point) {
        int x = point.getX();
        int y = point.getY();
        if (x < maxX && x >= 0 && y < maxX && y >= 0 && aStarGrid[x][y].isPassable()) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Method to check whether a coordinate is in the open or closed list
     *
     * @param coordinate    The coordinate to check
     * @param list          The list to check
     * @return              Whether the coordinate is in the list
     */
    private Boolean checkList(Coordinates coordinate, ArrayList<Coordinates> list) {
        for (Coordinates listCoord : list) {
            if (listCoord.isEqual(coordinate)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Method to update the values of a position in the A* Grid
     *
     * @param point         The point to update
     * @param pathCost      The new path cost of the point to update
     * @param heuristicCost The new heuristic cost of the point to update
     * @param parent        The new parent of the point to update
     */
    private void updateValues(Coordinates point, int pathCost, double heuristicCost, Coordinates parent) {
        aStarGrid[point.getX()][point.getY()].setPathCost(pathCost);
        aStarGrid[point.getX()][point.getY()].setHeuristicCost(heuristicCost);
        aStarGrid[point.getX()][point.getY()].updateTotalCost();
        aStarGrid[point.getX()][point.getY()].setParent(parent);
    }

    /*
     * Method to get the found path, working backwards from the goal point using the parent point
     * stored in the aStarGrid for each point
     *
     * @return The shortest path, stored in an ArrayList
     */
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