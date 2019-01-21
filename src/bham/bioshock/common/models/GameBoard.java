package bham.bioshock.common.models;

import bham.bioshock.common.consts.GridPoint;

/**
 * Stores the data required for the main game board
 */
public class GameBoard {
    /**
     * A grid containing the locations of all the planets, players, fuel boxes etc
     */
    private GridPoint[][] grid;

    /** A list of players */
    private Player[] players;

    /**
     * The ID of the player that the client is controlling, only used client-side
     */
    private int playerId;

    public GridPoint[][] getGrid() {
        return grid;
    }

    public void setGrid(GridPoint[][] grid) {
        this.grid = grid;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}