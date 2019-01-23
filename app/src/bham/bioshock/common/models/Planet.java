package bham.bioshock.common.models;

import java.util.UUID;

import bham.bioshock.common.models.Player;

/**
 * Stores the data of a player on the game board
 */
public class Planet {
    /** ID of the planet */
    private UUID id;

    /** Name of the planet */
    private String name;

    /** Location of the planet */
    private Coordinates coordinates;

    /** If captured, specifies the player that has captured the planet */
    private Player playerCaptured = null;

    public Planet(String name, Coordinates coordinates) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.coordinates = coordinates;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Player getPlayerCaptured() {
        return playerCaptured;
    }

    public void setPlayerCaptured(Player playerCaptured) {
        this.playerCaptured = playerCaptured;
    }
}