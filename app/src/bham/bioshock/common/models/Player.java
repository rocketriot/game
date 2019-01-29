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

    /** Player's textureID */
    private int textureID;

    public Player(Coordinates coordinates) {
        this.id = UUID.randomUUID();
        this.coordinates = coordinates;
        this.textureID = 0;
    }

    public Player(Coordinates coordinates, boolean isCpu) {
        this.id = UUID.randomUUID();
        this.coordinates = coordinates;
        this.isCpu = isCpu;
        this.textureID = 0;
    }

    public UUID getId() {
        return id;
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

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }
}