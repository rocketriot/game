package bham.bioshock.common.models;

import bham.bioshock.common.Direction;

import java.io.Serializable;
import java.util.ArrayList;

public class BoardMove implements Serializable {
    private ArrayList<Coordinates> position;
    private ArrayList<Direction> directions;
    private Coordinates startCoords, endCoords;

    public BoardMove(ArrayList<Direction> directions, ArrayList<Coordinates> position, Coordinates startCoords, Coordinates endCoords) {
        this.directions = directions;
        this.position = position;
        this.endCoords = endCoords;
        this.startCoords = startCoords;
    }

    public ArrayList<Direction> getDirections() {
        return directions;
    }

    public ArrayList<Coordinates> getPosition() {
        return position;
    }

    public Coordinates getStartCoords() {
        return startCoords;
    }

    public Coordinates getEndCoords() {
        return endCoords;
    }
}
