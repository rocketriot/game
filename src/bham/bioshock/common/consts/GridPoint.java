package bham.bioshock.common.consts;

import java.util.UUID;

/**
 * Specifies what is at the location of a grid
 */
public class GridPoint {
    /** Types of the grid point */
    public enum Type {
        PLAYER, PLANET, ASTROID, FUEL, EMPTY
    }

    /** The type of the grid point */
    public Type type;

    /** The value of the grid point i.e. a Player or a Planet */
    public Object value;

    GridPoint(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    GridPoint(Type type) {
        this.type = type;
    }
}