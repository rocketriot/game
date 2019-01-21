package bham.bioshock.common.models;

import java.util.UUID;

import bham.bioshock.common.models.Player;

/**
 * Stores the data of a player on the game board
 */
public class Planet {
    /** ID of the planet */
    public UUID id;

    /** Name of the planet */
    public String name;

    /** Location of the planet */
    public Coordinates coordinates;

    /** If captured, specifies the player that has captured the planet */
    public Player player_captured = null;

    public Planet(String name, Coordinates coordinates) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.coordinates = coordinates;
    }

    /** Changes the player that has captured that planet */
    public void setCaptured(Player player) {
        player_captured = player;
    }
}