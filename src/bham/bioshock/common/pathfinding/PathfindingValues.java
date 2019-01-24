package bham.bioshock.common.pathfinding;

import bham.bioshock.common.models.Coordinates;

// values that will be used during pathfinding - heuristics etc
public class PathfindingValues {

    private final int TRANSITION_COST = 1;

    // the actual value to take the path up to this point
    private int pathCost;

    // the heuristic estimate of the cost to get to the goal point
    private int heuristicCost;

    // the total cost (pathCost + heuristicCost) to go from this node to the goal node
    private int totalCost;

    // the parent point to this point - the point that came before
    private Coordinates parent;

    // whether the point cam be moved through
    private boolean passable;

    // initialise the values
    public PathfindingValues(int pathCost, int heuristicCost, int xPos, int yPos, boolean passable) {
        if (passable) {
            setPathCost(pathCost);
            setHeuristicCost(heuristicCost);
            updateTotalCost();
            setParent(xPos, yPos);
            this.passable = true;
        } else {
            setPathCost(Integer.MAX_VALUE);
            setHeuristicCost(Integer.MAX_VALUE);
            setTotalCost(Integer.MAX_VALUE);
            setParent(xPos, yPos);
            this.passable = false;
        }
    }

    // method to get the pathCost
    public int getPathCost() {
        return pathCost;
    }

    // method to get the heuristicCost
    public int getHeuristicCost() {
        return heuristicCost;
    }

    // method to get the totalCost
    public int getTotalCost() {
        return totalCost;
    }

    // method to get the parent
    public Coordinates getParent() {
        return parent;
    }

    // method to get whether the point is passable
    public boolean isPassable() {
        return passable;
    }

    // method to get the transition cost
    public int getTransitionCost(){
        return TRANSITION_COST;
    }

    // method to set the pathCost
    public void setPathCost(int pathCost){
        this.pathCost = pathCost;
    }

    // method to set the heuristicCost
    public void setHeuristicCost(int heuristicCost){
        this.heuristicCost = heuristicCost;
    }

    // method to set the totalCost
    public void setTotalCost(int totalCost){
        this.totalCost = totalCost;
    }

    // method to automatically set the total cost according to the path and heuristic costs
    public void updateTotalCost(){
        setTotalCost(pathCost + heuristicCost);
    }

    // method to set the parent
    public void setParent(int xPos, int yPos) {
        parent.setX(xPos);
        parent.setY(yPos);
    }

}