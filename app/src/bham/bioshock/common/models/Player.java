package bham.bioshock.common.models;

import java.util.UUID;

/**
 * Stores the data of a player on the game board
 */
public class Player {
    /** ID of the player */
    private UUID id;

    /** Username of the player */
    private String username;

    /** Location of the player */
    private Coordinates coordinates;

    /** The amount of fuel the player has left */
    private float fuel = 100.0f;

    /** The number of planets the player has captured */
    private int planetsCaptured = 0;

    /** Specifies if the player is controlled by AI */
    private boolean isCpu = false;

    public Player(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
    }

    public Player(boolean isCpu) {
        this.id = UUID.randomUUID();
        this.isCpu = isCpu;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }

    public int getPlanetsCaptured() {
        return planetsCaptured;
    }

    public void setPlanetsCaptured(int planetsCaptured) {
        this.planetsCaptured = planetsCaptured;
    }

    public boolean isCpu() {
        return isCpu;
    }

    public void setCpu(boolean cpu) {
        isCpu = cpu;
    }
}