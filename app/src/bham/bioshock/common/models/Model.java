package bham.bioshock.common.models;

/**
 * Stores all of the models
 */
public class Model {
    private GameBoard gameBoard;
    private boolean inGame;

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }
}