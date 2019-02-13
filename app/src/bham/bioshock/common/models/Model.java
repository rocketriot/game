package bham.bioshock.common.models;

import java.util.ArrayList;
import java.util.UUID;

/** Stores all of the models */
public class Model {
  /** Max number of players in a game */
  public final int MAX_PLAYERS = 4;
  
  /** Contains all of the information about the game board */
  private GameBoard gameBoard = new GameBoard();
  
  /** A list of players */
  private ArrayList<Player> players = new ArrayList<>(MAX_PLAYERS);

  /** The ID of the player that the client is controlling, only used client-side */
  private UUID mainPlayerId;

  /** The game's round */
  private int round = 0;
  
  /** The next player's turn */
  private int turn = 0;

  public void generateGrid() {
    // Set coordinates of the players
    int last = gameBoard.GRID_SIZE - 1;
    players.get(0).setCoordinates(new Coordinates(0, 0));
    players.get(1).setCoordinates(new Coordinates(0, last));
    players.get(2).setCoordinates(new Coordinates(last, last));
    players.get(3).setCoordinates(new Coordinates(last, 0));

    gameBoard.generateGrid();
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

  public void setPlayers(ArrayList<Player> players) {
    this.players = players;
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public void updatePlayer(Player updatingPlayer) {
    for (Player player : players) {
      if (player.getId() == updatingPlayer.getId()) player = updatingPlayer;
    }
  }

  public Player getMainPlayer() {
    for (Player player : players) {
      if (player.getId().equals(mainPlayerId))
        return player;
    }

    return null;
  }

  public void setMainPlayer(Player player) {
    this.mainPlayerId = player.getId();
  }

  public int getRound() {
    return round;
  }

  public int getTurn() {
    return turn;
  }

  /** After a player has finished their turn, set the next turn */
  public void nextTurn() {    
    // If all players have had their turn, go to next round
    if (++turn == MAX_PLAYERS) {
      round++;
      turn = 0;
    }
  }
}
