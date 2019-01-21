package bham.bioshock.common.models;

import java.util.UUID;

/**
 * Stores the data of a fuel box
 */
public class Fuel {
    /** ID of the fuel */
    public UUID id;

    /** Location of the fuel */
    public Coordinates coordinates;

    /** The amount of fuel that the fuel box holds */
    public float value = 20.0f;

    public Fuel(Coordinates coordinates) {
        this.id = UUID.randomUUID();
        this.coordinates = coordinates;
    }
}