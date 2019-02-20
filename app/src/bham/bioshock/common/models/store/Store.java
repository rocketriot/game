package bham.bioshock.common.models.store;

import bham.bioshock.client.AppPreferences;
import java.util.HashMap;
import java.util.Map.Entry;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import com.badlogic.gdx.Screen;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
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
  private GameBoard gameBoard = new GameBoard();
  /** A hash map of players to their userID */
  private HashMap<UUID, Player> players = new HashMap<>();

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

  public void generateGrid() {
    // Set coordinates of the players
    int last = gameBoard.GRID_SIZE - 1;
    Player[] list = (Player[]) players.values().toArray();
    list[0].setCoordinates(new Coordinates(0, 0));
    list[1].setCoordinates(new Coordinates(0, last));
    list[2].setCoordinates(new Coordinates(last, last));
    list[3].setCoordinates(new Coordinates(last, 0));

    gameBoard.generateGrid();
  }

  public GameBoard getGameBoard() {
    return gameBoard;
  }

  public Screen getScreen() {
    return currentScreen;
  }

  public boolean isMainPlayer(UUID id) {
    if (mainPlayerId == null) {
      return false;
    }
    return mainPlayerId.equals(id);
  }

  public void setGameBoard(GameBoard gameBoard) {
    this.gameBoard = gameBoard;
  }

  public void setScreen(Screen screen) {
    currentScreen = screen;
  }

  public Collection<Player> getPlayers() {
    return players.values();
  }

  public Player getPlayer(UUID id) {
    return players.get(id);
  }

  public void setPlayers(ArrayList<Player> ps) {
    this.players.clear();
    for (Player p : ps) {
      players.put(p.getId(), p);
    }
  }

  public void addPlayer(Player player) {
    players.put(player.getId(), player);
  }

  public void removePlayer(UUID id) {
    players.remove(id);
  }

  public void removeAllPlayers() {
    players.clear();
  }

  public void updatePlayer(Player updatingPlayer) {
    players.put(updatingPlayer.getId(), updatingPlayer);
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
    Player[] list = (Player[]) players.values().toArray();
    return list[turn];
  }

  /** After a player has finished their turn, set the next turn */
  public void nextTurn() {
    // If all players have had their turn, go to next round
    if (++turn == MAX_PLAYERS) {
      round++;
      turn = 0;

      // Increase player's fuel after each round
      for (Entry<UUID, Player> entry : players.entrySet()) {
        entry.getValue().increaseFuel(30.0f);
      }
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

  public void resetMinigameStore() {
    minigameStore = null;
  }

  /** Check if it is the main player's turn */
  public boolean isMainPlayerTurn() {
    // TODO Auto-generated method stub
    return false;
  }
}
