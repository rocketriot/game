package bham.bioshock.common.consts;

/**
 * Specifies what is at the location of a grid
 */
public class GridPoint {
    /** Types of the grid point */
    public enum Type {
        PLAYER, PLANET, ASTROID, FUEL, EMPTY
    }

    /** The type of the grid point */
    private Type type;

    /** The value of the grid point i.e. a Player or a Planet */
    private Object value;

    public GridPoint(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public GridPoint(Type type) {
        this.type = type;
    }

    public GridPoint.Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setType(GridPoint.Type type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}