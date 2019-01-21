package bham.bioshock.common.models;

import bham.bioshock.common.consts.GridPoint;

/**
 * Stores the data required for the main game board
 */
public class GameBoard {
    /**
     * A grid containing the locations of all the planets, players, fuel boxes etc
     */
    public GridPoint[][] grid;

    /** A list of players */
    public Player[] players;

    /**
     * The ID of the player that the client is controlling, only used client-side
     */
    public int playerId;
}