package bham.bioshock.common.models.store;

import java.util.*;
import com.badlogic.gdx.Screen;
import com.google.inject.Singleton;
import bham.bioshock.client.AppPreferences;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.models.Player;
import bham.bioshock.minigame.models.Entity;

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
  /** the maximum number of rounds */
  public int maxRounds = 1;
  /** The next player's turn */
  private int turn = 0;
  /** If the game is reconnecting with the server */
  private boolean reconnecting = false;

  /** The username of whoever just won a minigame */
  private String minigameWinner = null;

  /** Minigame World */
  private MinigameStore minigameStore;

  /** Communication store */
  private CommunicationStore communicationStore = new CommunicationStore();

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
    for (Player p : ps) {
      if (!players.contains(p)) {
        players.add(p);
      }
    }
  }

  public void overwritePlayers(ArrayList<Player> newPlayers) {
    players.clear();
    players = newPlayers;
  }

  public void addPlayer(Player player) {
    if (getPlayer(player.getId()) == null) {
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

  public UUID getMainPlayerId() {
    return mainPlayerId;
  }

  public void setMainPlayer(UUID playerId) {
    this.mainPlayerId = playerId;
  }

  public int getRound() {
    return round;
  }

  public int getTurn() {
    return turn;
  }

  public void setTurn(int turn) {
    this.turn = turn;
  }

  public void setRound(int round) {
    this.round = round;
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

  public UUID getPlanetOwner(UUID planetId) {
    if (planetId == null)
      return null;
    Planet planet = gameBoard.getPlanet(planetId);
    Player p = planet.getPlayerCaptured();
    return p == null ? null : p.getId();
  }

  /**
   * Returns whether it's the main player's turn
   * 
   * @return Whether it's the mainPlayer's turn
   */
  public boolean isMainPlayersTurn() {
    return getMainPlayer().equals(getMovingPlayer());
  }

  /**
   * Returns whether it's the passed in player's turn
   * 
   * @param player the player being checked
   * @return Whether it's the passed in player's turns
   */
  public boolean isThisPlayersTurn(Player player) {
    return player.equals(getMovingPlayer());
  }

  /** After a player has finished their turn, set the next turn */
  public void nextTurn() {
    // If all players have had their turn, go to next round
    if (++turn == MAX_PLAYERS) {
      round++;
      turn = 0;

      // Increase player's fuel after each round and add points for each planet owned
      for (Player p : players) {
        p.newRound();
      }
    }
  }


  public ArrayList<Player> getWinner() {
    int maxScore = 0;
    ArrayList<Player> winners = new ArrayList<Player>();

    for (Player player : players) {
      if (player.getPoints() > maxScore)
        maxScore = player.getPoints();
    }

    for (Player player : players)
      if (player.getPoints() == maxScore)
        winners.add(player);

    // will return the winner or the winners if its a tie;
    return winners;

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
    if(minigameStore != null) {
      minigameStore.dispose();      
    }

    minigameStore = null;
  }

  public List<UUID> getCpuPlayers() {
    List<UUID> cpuPlayers = new ArrayList<>();
    for (Player p : players) {
      if (p.isCpu()) {
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

  public void setHost(boolean value) {
    communicationStore.setHost(value);
  }

  public boolean isHost() {
    return communicationStore.isHost();
  }

  public String getMinigameWinner() {
    return minigameWinner;
  }

  public void setMinigameWinner(String minigameWinner) {
    this.minigameWinner = minigameWinner;
  }

  public CommunicationStore getCommStore() {
    return communicationStore;
  }

  public void setMaxRounds(int n) {
    this.maxRounds = n;
  }

  public int getMaxRounds() {
    return this.maxRounds;
  }

  /**
   * Sorts the arraylist descending on the players scores
   * 
   * @return arraylist of the players sorted descending on their score
   */

  public ArrayList<Player> getSortedPlayers(){
    ArrayList<Player> sorted = new ArrayList<>(players);
    sorted.sort(Comparator.comparingInt(Player::getPoints).reversed());

    return sorted;
  }
}
