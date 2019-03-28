package bham.bioshock.common.pathfinding;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/** The A-Star pathfinding algorithm. */
public class AStarPathfinding {

  private static final Logger logger = LogManager.getLogger(AStarPathfinding.class);

  /** The cost to go from one point to another */
  private final int TRANSITION_COST = 1;

  /** Start position of the path */
  private Coordinates startPosition;

  /** Goal position of the path */
  private Coordinates goalPosition;

  /** The game grid at the time of the pathfinding search */
  private GridPoint[][] gameGrid;

  /** The grid that will contain the pathfinding values for each point */
  private PathfindingValues[][] aStarGrid;

  /** The maximum x value of the map */
  private int maxX;

  /** The maximum y value of the map */
  private int maxY;

  /**
   * Method to instantiate the AStarPathfinding object
   *
   * @param grid The grid from the game board to pathfind on
   * @param startPosition The position that the pathfinding needs to start from
   * @param maxX The maximum x value of the grid, i.e. the x limit of the game map
   * @param maxY The maximum y value of the grid, i.e. the y limit of the game map
   * @param players The list of players that are playing
   */
  public AStarPathfinding(
      GridPoint[][] grid,
      Coordinates startPosition,
      int maxX,
      int maxY,
      ArrayList<Player> players) {
    setStartPosition(startPosition);
    this.maxX = maxX;
    this.maxY = maxY;
    setGameGrid(grid, players);
  }

  /**
   * Method to find the path between the start position and the inputted goal position
   *
   * @param goalPosition The position on the grid that the path needs to end at
   * @return The path that the algorithm has found to be the shortest
   */
  public ArrayList<Coordinates> pathfind(Coordinates goalPosition) {
    setGoalPosition(goalPosition);
    aStarGrid = setAStarGrid(); // set the A* Grid to be the current grid
    aStarGrid[startPosition.getX()][startPosition.getY()].setTotalCost(0);

    ArrayList<Coordinates> openList = new ArrayList<>(); // list of all generated points
    ArrayList<Coordinates> closedList = new ArrayList<>(); // list of all expanded points
    insertIntoList(openList, startPosition); // add the start position to the open list

    // while there are points to check
    while (!openList.isEmpty()) {
      Coordinates currentPosition =
          findMinPoint(openList); // get coordinates of point with smallest cost
      openList.remove(currentPosition);
      closedList.add(currentPosition);
      ArrayList<Coordinates> successors = generateSuccessors(currentPosition, closedList);

      // iterate through the successors
      for (Coordinates currentSuccessor : successors) {
        int x = currentSuccessor.getX();
        int y = currentSuccessor.getY();
        // check if the goal has been found
        if (goalPosition.isEqual(currentSuccessor)) {
          aStarGrid[goalPosition.getX()][goalPosition.getY()].setParent(currentPosition);
          return getPath();
        } else { // otherwise, calculate pathfinding values
          int successorPathCost = aStarGrid[x][y].getPathCost() + TRANSITION_COST;
          double successorHeuristicCost = findHeuristic(currentSuccessor);
          double totalCost = successorPathCost + successorHeuristicCost;

          if (checkList(
              currentSuccessor, openList)) { // check if the successor is already in the open list
            double openCost = aStarGrid[x][y].getTotalCost();
            // check if the total cost calculated is less than the one stored for the point
            if (openCost > totalCost) { // if so, update the values
              updateValues(
                  currentSuccessor, successorPathCost, successorHeuristicCost, currentPosition);
            }
          } else if (checkList(
              currentSuccessor, closedList)) { // else check if it is already in the closed list
            double closedCost = aStarGrid[x][y].getTotalCost();
            // check if the total cost calculated is less than the one stored for the point
            if (closedCost > totalCost) { // if so, update the values and move point to open list
              updateValues(
                  currentSuccessor, successorPathCost, successorHeuristicCost, currentPosition);
              closedList.remove(currentSuccessor);
              openList.add(currentSuccessor);
            }
          } else { // otherwise, update the values and add to the open list
            updateValues(
                currentSuccessor, successorPathCost, successorHeuristicCost, currentPosition);
            openList.add(currentSuccessor);
          }
        }
      }
      closedList.add(currentPosition);
    }
    return new ArrayList<>(); // if there is no path then return an empty arraylist
  }

  /**
   * Method to set the game grid that will be used for pathfinding. Needs to also get the positions
   * of each player in the passed list so that they can be added to the grid.
   *
   * @param grid The grid that will be used
   * @param players The list of players that are currently playing
   */
  public void setGameGrid(GridPoint[][] grid, ArrayList<Player> players) {
    gameGrid = Arrays.stream(grid).map(r -> r.clone()).toArray(GridPoint[][]::new);

    // get the position of each player and add them into the grid
    for (Player player : players) {
      Coordinates playerCoords = player.getCoordinates();
      if (playerCoords == null) {
        logger.fatal("Player " + player.getUsername() + " coordinates are null!");
        continue;
      }
      gameGrid[playerCoords.getX()][playerCoords.getY()] = new GridPoint(GridPoint.Type.PLAYER, 0);
    }
  }

  /**
   * Method to set the aStarGrid to the current grid. This grid stores the heuristic values that the
   * pathfinding algorithm uses to determine which point to go to next, as well as whether each
   * point is passable and the parent of each point.
   */
  private PathfindingValues[][] setAStarGrid() {
    PathfindingValues[][] tempGrid = new PathfindingValues[maxX][maxY]; // temporary grid to return

    // go through the passed gameGrid and assign values accordingly to the temporary grid
    for (int x = 0; x < maxX; x++) {
      for (int y = 0; y < maxY; y++) {
        if (gameGrid[x][y].getType().isValidForPlayer()) {
          tempGrid[x][y] = new PathfindingValues(true);
        } else {
          tempGrid[x][y] = new PathfindingValues(false);
        }
      }
    }
    return tempGrid;
  }

  /**
   * Method to insert a value into the required list - either the open or closed list
   *
   * @param list The list to add to
   * @param coords The coordinates to add
   */
  private void insertIntoList(ArrayList<Coordinates> list, Coordinates coords) {
    list.add(coords);
  }

  /**
   * Method to find the point in a list that has the smallest heuristic value for expansion
   *
   * @param list The list that contains the coordinates to be searched through
   * @return The Coordinate value of the point with the smallest heuristic value
   */
  private Coordinates findMinPoint(ArrayList<Coordinates> list) {
    double minimumCost = Integer.MAX_VALUE;
    Coordinates nextPoint = new Coordinates(Integer.MAX_VALUE, Integer.MAX_VALUE);

    for (Coordinates currentCoords : list) {
      PathfindingValues value = aStarGrid[currentCoords.getX()][currentCoords.getY()];

      if (value.isPassable()
          || currentCoords == startPosition) { // check if the point can be traversed to
        // check if the current point has a lower cost than the current lowest cost point
        if (value.getTotalCost() < minimumCost) {
          minimumCost = value.getTotalCost();
          nextPoint = currentCoords;
        }
      }
    }
    return nextPoint;
  }

  /**
   * Method to find the heuristic value for a given point - uses the cross product of the distance
   * from the start to the goal and the current to the goal to prefer straight paths.
   *
   * @param position The coordinates of the point you want to calculate the heuristic value for
   * @return The heuristic value found
   */
  private double findHeuristic(Coordinates position) {
    int h = 0;
    int dx1 = position.getX() - goalPosition.getX();
    int dy1 = position.getY() - goalPosition.getY();
    int dx2 = startPosition.getX() - goalPosition.getX();
    int dy2 = startPosition.getY() - goalPosition.getY();
    int cross = Math.abs(dx1 * dy2 - dx2 * dy1);
    h += cross * 0.001;
    return h;
  }

  /**
   * Method to generate the successors of a given point in all 4 directions, checking if they are
   * valid first
   *
   * @param currentPoint The coordinates of the current point to generate the successors for
   * @param closedList The closed list, containing all the already closed nodes that you do not want
   *     to become successors to another node
   * @return A list of the valid successors found
   */
  private ArrayList<Coordinates> generateSuccessors(
      Coordinates currentPoint, ArrayList<Coordinates> closedList) {
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

  /**
   * Method to check whether the point is valid - is within the board coordinates and / or is
   * passable
   *
   * @param point The coordinates of the point to check
   * @return Whether the point is valid or not
   */
  private Boolean isValid(Coordinates point) {
    int x = point.getX();
    int y = point.getY();
    return x < maxX && x >= 0 && y < maxX && y >= 0 && aStarGrid[x][y].isPassable();
  }

  /**
   * Method to check whether a coordinate is in the open or closed list
   *
   * @param coordinate The coordinate to check
   * @param list The list to check
   * @return Whether the coordinate is in the list
   */
  private Boolean checkList(Coordinates coordinate, ArrayList<Coordinates> list) {
    for (Coordinates listCoord : list) {
      if (listCoord.isEqual(coordinate)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Method to update the values of a position in the A* Grid
   *
   * @param point The point to update
   * @param pathCost The new path cost of the point to update
   * @param heuristicCost The new heuristic cost of the point to update
   * @param parent The new parent of the point to update
   */
  private void updateValues(
      Coordinates point, int pathCost, double heuristicCost, Coordinates parent) {
    aStarGrid[point.getX()][point.getY()].setPathCost(pathCost);
    aStarGrid[point.getX()][point.getY()].setHeuristicCost(heuristicCost);
    aStarGrid[point.getX()][point.getY()].updateTotalCost();
    aStarGrid[point.getX()][point.getY()].setParent(parent);
  }

  /**
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
      if (!gameGrid[currentPoint.getX()][currentPoint.getY()].isType(GridPoint.Type.PLANET)) {
        path.add(currentPoint);
      }
      currentPoint = aStarGrid[currentPoint.getX()][currentPoint.getY()].getParent();
    }
    path.add(startPosition);
    Collections.reverse(path);
    return path;
  }

  /**
   * Method to set the start position of the pathfinding algorithm
   *
   * @param startPosition The position that the pathfinding needs to start from
   */
  public void setStartPosition(Coordinates startPosition) {
    this.startPosition = startPosition;
  }

  /**
   * Method to set the goal position of the pathfinding algorithm
   *
   * @param goalPosition The position that the pathfinding needs to get to
   */
  public void setGoalPosition(Coordinates goalPosition) {
    this.goalPosition = goalPosition;
  }
}
