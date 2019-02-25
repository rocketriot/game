package bham.bioshock.common.models.store;

import bham.bioshock.client.AppPreferences;

import java.util.HashMap;

import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import com.badlogic.gdx.Screen;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.UUID;

/** Stores all of the models */
@Singleton
public class Store {

  /** Max number of players in a game */
  // FOR TESTING
  public final int MAX_PLAYERS = 4;

  private AppPreferences preferences;
  private Screen currentScreen;
  /** Contains all of the information about the game board */
  private GameBoard gameBoard = null;
  /** A list of players */
  private ArrayList<Player> players = new ArrayList<>(MAX_PLAYERS);

  /** A hash map of players to their userID */
  private HashMap<UUID, Player> playersMap = new HashMap<>();

  /** The ID of the player that the client is controlling, only used client-side */
  private UUID mainPlayerId;
  /** The game's round */
  private int round = 0;
  /** The next player's turn */
  private int turn = 0;

  /** Minigame World */
  private MinigameStore minigameStore;

  public AppPreferences getPreferences() {
    return preferences;
  }

  public GameBoard getGameBoard() {
    return gameBoard;
  }
  
  public void setGameBoard(GameBoard gameBoard) {
    this.gameBoard = gameBoard;
  }

  public Screen getScreen() {
    return currentScreen;
  }

  public boolean isMainPlayer(UUID id) {
    if(mainPlayerId == null) {
      return false;
    }
    return mainPlayerId.equals(id);
  }

  public void setScreen(Screen screen) {
    currentScreen = screen;
  }

  public ArrayList<Player> getPlayers() {
    return players;
  }

  public HashMap<UUID, Player> getPlayersMap() { return playersMap; }

  public Player getPlayer(UUID id) {
    return playersMap.get(id);
  }

  public void setPlayers(ArrayList<Player> players) {
    this.players.clear();
    this.playersMap.clear();
    this.players = players;
    for(Player p : players) {
      playersMap.put(p.getId(), p);
    }
  }

  public void addPlayer(Player player) {
    playersMap.put(player.getId(), player);
    players.add(player);
  }

  public void removePlayer(UUID id) {
    players.removeIf(p -> p.getId().equals(id));
    playersMap.remove(id);
  }

  public void removeAllPlayers() {
    players.clear();
    playersMap.clear();
  }

  public void updatePlayer(Player updatingPlayer) {
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getId().equals(updatingPlayer.getId())) players.set(i, updatingPlayer);
    }
  }

  public Player getMainPlayer() {
    return getPlayer(mainPlayerId);
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

  /** Get's the player who's turn it is */
  public Player getMovingPlayer() {
    return players.get(turn);
  }

  /** After a player has finished their turn, set the next turn */
  public void nextTurn() {
    // If all players have had their turn, go to next round
    if (++turn == MAX_PLAYERS) {
      round++;
      turn = 0;

      // Increase player's fuel after each round
      for (Player player : players) player.increaseFuel(30.0f);
    }
  }

  /*
   * MINIGAME
   */
  public void setMinigameStore(MinigameStore store) {
    this.minigameStore = store;
  }
  public MinigameStore getMinigameStore() {
    return this.minigameStore;
  }
  public void resetMinigameStore(){
    minigameStore = null;
  }
}
