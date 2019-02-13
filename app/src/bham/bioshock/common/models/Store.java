package bham.bioshock.common.models;

import java.util.ArrayList;
import com.google.inject.*;
import bham.bioshock.client.AppPreferences;
import bham.bioshock.client.screens.ScreenMaster;
import java.util.UUID;

/** Stores all of the models */

@Singleton
public class Store {
  
  private AppPreferences preferences;
  
  private ScreenMaster currentScreen;
  
  @Inject
  public Store() {
    this.preferences = new AppPreferences();
  }

  /** Max number of players in a game */
  public final int MAX_PLAYERS = 1;
  /** Contains all of the information about the game board */
  private GameBoard gameBoard = new GameBoard();
  /** A list of players */
  private ArrayList<Player> players = new ArrayList<>(MAX_PLAYERS);

  /** The ID of the player that the client is controlling, only used client-side */
  private UUID mainPlayerId;
  
  public AppPreferences getPreferences() {
    return preferences;
  }
  
  public GameBoard generateGrid() throws Exception {
    gameBoard.generateGrid(players);
    return gameBoard;
  }

  public GameBoard getGameBoard() {
    return gameBoard;
  }
  
  public void setScreen(ScreenMaster screen) {
    currentScreen = screen;
  }

  public ScreenMaster getScreen() {
    return currentScreen;
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

  public Player getMainPlayer() {
    // Might be too slow
    for (Player player : players) {
      if (player.getId() == mainPlayerId) return player;
    }

    return null;
  }

  public void setMainPlayer(Player player) {
    this.mainPlayerId = player.getId();
  }
}
