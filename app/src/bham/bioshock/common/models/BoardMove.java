package bham.bioshock.common.models;

import bham.bioshock.common.Direction;

import java.io.Serializable;
import java.util.ArrayList;

public class BoardMove implements Serializable {
    private ArrayList<Integer> distance;
    private ArrayList<Direction> directions;
    private Coordinates startCoords, endCoords;

    public BoardMove(ArrayList<Direction> directions, ArrayList<Integer> distance, Coordinates startCoords, Coordinates endCoords) {
        this.directions = directions;
        this.distance = distance;
        this.endCoords = endCoords;
        this.startCoords = startCoords;
    }

    public ArrayList<Direction> getDirections() {
        return directions;
    }

    public ArrayList<Integer> getDistance() {
        return distance;
    }

    public Coordinates getStartCoords() {
        return startCoords;
    }

    public Coordinates getEndCoords() {
        return endCoords;
    }
}
