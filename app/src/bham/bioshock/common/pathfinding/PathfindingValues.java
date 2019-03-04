package bham.bioshock.common.pathfinding;

import bham.bioshock.common.models.Coordinates;

/**
 * The type PathfindingValues.
 */
public class PathfindingValues {

  /**
   * The actual value to take the path up to this point
   */
  private int pathCost;

  /**
   * The heuristic estimate of the cost to get to the goal point
   */
  private double heuristicCost;

  /**
   * The total cost (pathCost + heuristicCost) to go from this node to the goal node
   */
  private double totalCost;

  /**
   * The parent point to this point - the point that came before
   */
  private Coordinates parent;

  /**
   * Whether the point cam be moved through
   */
  private boolean passable;

  /**
   * Instantiates a new Pathfinding values.
   *
   * @param passable whether the point is passable or not
   */
  public PathfindingValues(boolean passable) {
    this.passable = passable;
  }

  /**
   * Gets path cost.
   *
   * @return the path cost
   */
  public int getPathCost() {
    return pathCost;
  }

  /**
   * Gets heuristic cost.
   *
   * @return the heuristic cost
   */
  public double getHeuristicCost() {
    return heuristicCost;
  }

  /**
   * Gets total cost.
   *
   * @return the total cost
   */
  public double getTotalCost() {
    return totalCost;
  }

  /**
   * Gets the parent point.
   *
   * @return the parent point
   */
  public Coordinates getParent() {
    return parent;
  }

  /**
   * Returns whether a point is passable or not
   *
   * @return whether the point is passable as a boolean
   */
  public boolean isPassable() {
    return passable;
  }

  /**
   * Sets path cost.
   *
   * @param pathCost the path cost
   */
  public void setPathCost(int pathCost) {
    this.pathCost = pathCost;
  }

  /**
   * Sets heuristic cost.
   *
   * @param heuristicCost the heuristic cost
   */
  public void setHeuristicCost(double heuristicCost) {
    this.heuristicCost = heuristicCost;
  }

  /**
   * Sets total cost.
   *
   * @param totalCost the total cost
   */
  public void setTotalCost(double totalCost) {
    this.totalCost = totalCost;
  }

  /**
   * Updates total cost with the current path and heuristic costs.
   */
  public void updateTotalCost() {
    setTotalCost(pathCost + heuristicCost);
  }

  /**
   * Sets the parent point.
   *
   * @param parent the parent point
   */
  public void setParent(Coordinates parent) {
    this.parent = parent;
  }

  /**
   * Sets whether the point is passable.
   *
   * @param passable whether the point is passable
   */
  public void setPassable(Boolean passable) {
    this.passable = passable;
  }
}