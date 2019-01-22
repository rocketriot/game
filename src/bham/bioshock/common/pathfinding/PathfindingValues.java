package bham.bioshock.common.pathfinding;

// values that will be used during pathfinding - heuristics etc
public class PathfindingValues {

    // the actual value to take the path up to this point
    private int pathCost;

    // the total cost (pathCost + heuristicCost) to go from this node to the goal node
    private int totalCost;

    // the parent point to this point - the point that came before
    private int[][] parent;

    // initialise the values
    public PathfindingValues(int pathCost, int totalCost, int[][] parent) {
        this.pathCost = pathCost;
        this.totalCost = totalCost;
        this.parent = parent;
    }

    // method to update the values
    public void updateValues(int pathCost, int totalCost, int[][] parent){
        this.pathCost = pathCost;
        this.totalCost = totalCost;
        this.parent = parent;
    }

    // method to get the pathCost
    public int getPathCost(){
        return pathCost;
    }

    // method to get the totalCost
    public int getTotalCost(){
        return totalCost;
    }

    //method to get the parent
    public int [][] getParent(){
        return parent;
    }

}