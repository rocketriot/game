package bham.bioshock.common.models.store;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.badlogic.gdx.Screen;
import com.google.inject.Singleton;
import bham.bioshock.client.AppPreferences;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.models.Player;

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

  /** The ID of the player that the client is controlling, only used client-side */
  private UUID mainPlayerId;
  /** The game's round */
  private int round = 1;
  /** The next player's turn */
  private int turn = 0;
  /** If the game is reconnecting with the server */
  private boolean reconnecting = false;

  /** Minigame World */
  private MinigameStore minigameStore;

  /** Join Screen */
  private JoinScreenStore joinScreenStore;

  public AppPreferences getPreferences() {
    return preferences;
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

  public ArrayList<Player> getPlayers() {
    return players;
  }

  public Player getPlayer(UUID id) {
    return players.stream().filter(p -> p.getId().equals(id)).findAny().orElse(null);
  }

  public void savePlayers(ArrayList<Player> ps) {
    for(Player p : ps) {
      if(!players.contains(p)) {
        players.add(p);
      }
    }
  }

  public void addPlayer(Player player) {
    if(getPlayer(player.getId()) == null) {
      players.add(player);      
    }
  }

  public void removePlayer(UUID id) {
    players.removeIf(p -> p.getId().equals(id));
  }

  public void removeAllPlayers() {
    players.clear();
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
    return players.get(turn % players.size());
  }
  
  public void setPlanetOwner(UUID playerId, UUID planetId) {
    Planet planet = gameBoard.getPlanet(planetId);
    Player p = getPlayer(playerId);
    planet.setPlayerCaptured(p);
  }

  /**
   * Returns whether it's the main player's turn
   * @return Whether it's the mainPlayer's turn
   */
  public boolean isMainPlayersTurn() {
    return getMainPlayer().equals(getMovingPlayer());
  }

  /**
   * Returns whether it's the passed in player's turn
   * @param player the player being checked
   * @return Whether it's the passed in player's turns
   */
  public boolean isThisPlayersTurn(Player player) { return player.equals(getMovingPlayer()); }

  /** After a player has finished their turn, set the next turn */
  public void nextTurn() {
    // If all players have had their turn, go to next round
    if (++turn == MAX_PLAYERS) {
      round++;
      turn = 0;

      // Increase player's fuel after each round
      for (Player p : players) {
        p.increaseFuel(30.0f);
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

  public List<UUID> getCpuPlayers() {
    List<UUID> cpuPlayers = new ArrayList<>();
    for(Player p : players) {
      if(p.isCpu()) {
        cpuPlayers.add(p.getId());
      }
    }
    return cpuPlayers;
  }
  
  public JoinScreenStore getJoinScreenStore() {
    return joinScreenStore;
  }

  public void setJoinScreenStore(JoinScreenStore joinScreenStore) {
    this.joinScreenStore = joinScreenStore;
  }

  public void reconnecting(boolean isLoading) {
    this.reconnecting = isLoading;
  }
  
  public Boolean isReconnecting() {
    return reconnecting;
  } 
  

}
