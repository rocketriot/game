package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.screens.GameBoardScreen;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.communication.client.CommunicationClient;
import bham.bioshock.communication.interfaces.MessageService;
import bham.bioshock.communication.messages.boardgame.AddBlackHoleMessage;
import bham.bioshock.communication.messages.boardgame.EndTurnMessage;
import bham.bioshock.communication.messages.boardgame.GameBoardMessage;
import bham.bioshock.communication.messages.boardgame.MovePlayerOnBoardMessage;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.UUID;

/** Game board controller */
public class GameBoardController extends Controller {
  private MessageService clientService;
  private CommunicationClient commClient;
  private AssetContainer assets;

  @Inject
  public GameBoardController(Router router, AssetContainer assets, Store store,
      MessageService clientService, BoardGame game, CommunicationClient commClient) {
    super(store, router, game);
    this.clientService = clientService;
    this.router = router;
    this.commClient = commClient;
    this.assets = assets;
  }

  /** Start the game */
  public void show() {
    router.call(Route.FADE_OUT, "mainMenu");
    router.call(Route.START_MUSIC, "boardGame");

    Player player = store.getMainPlayer();
    commClient.saveToFile(player.getUsername(), player.getId());
    setScreen(new GameBoardScreen(router, store, assets));
    store.resetMinigameStore();
  }

  /** Handles when the server sends the game board to the client */
  public void saveGameBoard(GameBoard gameBoard) {
    store.setGameBoard(gameBoard);
  }

  /** 
   * Saves the players to the store 
   * @param players the players to save to the store
   */
  public void savePlayers(ArrayList<Player> players) {
    store.savePlayers(players);
  }
  
  /** Sets the max rounds of the game board */
  public void gameInit(GameBoardMessage data) {
    store.setMaxRounds(data.maxRounds);
  }
  
  public void setOwner(UUID[] planetOwner) {
    UUID playerId = planetOwner[0];
    UUID planetId = planetOwner[1];
    
    store.setPlanetOwner(playerId, planetId);
  }
  
  public void updateCoordinates(Coordinates[] coordinates) {
    ArrayList<Player> players = store.getPlayers();

    for (int i = 0; i < coordinates.length; i++) {
      players.get(i).setSpawnPoint(coordinates[i]);
      players.get(i).setCoordinates(coordinates[i]);
    }
  }

  /** 
   * Handles when a player attempts to make a movement on the game board
   * @param destination the coordinates for where the player would like to travel towards
   */
  public void move(Coordinates destination) {
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();
    Player mainPlayer = store.getMainPlayer();

    // Initialize path finding
    int gridSize = store.getGameBoard().GRID_SIZE;
    AStarPathfinding pathFinder = new AStarPathfinding(grid, mainPlayer.getCoordinates(), gridSize,
        gridSize, store.getPlayers());

    // pathsize - 1 since path includes start position
    ArrayList<Coordinates> path = pathFinder.pathfind(destination);
    float pathCost = (path.size() - 1) * mainPlayer.getFuelGridCost();

    // Handle if player doesn't have enough fuel
    if (mainPlayer.getFuel() < pathCost || pathCost == -10)
      return;

    // Send move request to the server
    clientService.send(new MovePlayerOnBoardMessage(destination, mainPlayer.getId(), null));
  }

  /** Ends the players turn */
  public void endTurn() {
    clientService.send(new EndTurnMessage());
  }

  /** Handles server message to end turn */
  public void updateTurn() {
    store.nextTurn();
  }

  /** Sends a new black hole to the server */
  public void addBlackHole(Coordinates coordinates) {
    clientService.send(new AddBlackHoleMessage(coordinates));
    store.getMainPlayer().addedBlackHole();
  }

  /** Receives a black hole from the server */
  public void blackHoleReceived(Coordinates coordinates) {
    store.getGameBoard().addBlackHole(new BlackHole(coordinates));
  }

  /**
   * Handles when a player travels over a black hole
   * @param player the player who travels to a black hole
   */
  public void movePlayerToBlackHole(Player player) {
    player.clearBoardMove();
    SoundController.playSound("blackHole");
    movePlayerToRandomPoint(player);
  }

  /**
   * Player move received from the server 
   * @param data the data of the player movement
   */
  public void moveReceived(MovePlayerOnBoardMessage data) {
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();

    // Get data from the message
    Coordinates goalCoords = data.coordinates;
    Player movingPlayer = store.getPlayer(data.id);

    // Initialize path finding
    int gridSize = store.getGameBoard().GRID_SIZE;
    AStarPathfinding pathFinder = new AStarPathfinding(grid, movingPlayer.getCoordinates(),
        gridSize, gridSize, store.getPlayers());

    ArrayList<Coordinates> path = pathFinder.pathfind(goalCoords);
    movingPlayer.createBoardMove(path);
    movingPlayer.setTeleportCoords(data.randomCoords);
  }

  /** 
   * Moves the player to a random position on the grid
   * @param player the player to randomly relocate
   */
  public void movePlayerToRandomPoint(Player player) {
    player.setCoordinates(player.getRandomCoords());
    router.call(Route.STOP_SOUND, "rocket");
    if (store.isMainPlayersTurn()) {
      endTurn();
    }
  }

  /** Specifies if the grid has been received from the server */
  public boolean hasReceivedGrid() {
    return store.getGameBoard().getGrid() != null;
  }

}
