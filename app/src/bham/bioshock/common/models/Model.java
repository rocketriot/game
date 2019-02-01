package bham.bioshock.common.models;

import java.util.ArrayList;

/**
 * Stores all of the models
 */
public class Model {
    /** Contains all of the information about the game board */
    private GameBoard gameBoard;

    /** A list of players */
    private ArrayList<Player> players = new ArrayList<>();

    /**
     * The ID of the player that the client is controlling, only used client-side
     */
    private int playerId;

    public GameBoard createGameBoard() {
        gameBoard = new GameBoard();
        return gameBoard;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}