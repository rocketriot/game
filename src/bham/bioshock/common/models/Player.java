package bham.bioshock.common.models;

import java.util.UUID;

/**
 * Stores the data of a player on the game board
 */
public class Player {
    /** ID of the player */
    public UUID id;

    /** Location of the player */
    public Coordinates coordinates;

    /** The amount of fuel the player has left */
    public float fuel = 100.0f;

    /** The number of planets the player has captured */
    public int planetsCaptured = 0;

    /** Specifies if the player is controlled by AI */
    public boolean isCpu = false;

    public Player(Coordinates coordinates) {
        this.id = UUID.randomUUID();
        this.coordinates = coordinates;
    }

    public Player(Coordinates coordinates, boolean isCpu) {
        this.id = UUID.randomUUID();
        this.coordinates = coordinates;
        this.isCpu = isCpu;
    }
}