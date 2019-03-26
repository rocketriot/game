package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.GameBoardScreen;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.communication.client.CommunicationClient;
import bham.bioshock.communication.interfaces.MessageService;
import bham.bioshock.communication.messages.boardgame.AddBlackHoleMessage;
import bham.bioshock.communication.messages.boardgame.EndTurnMessage;
import bham.bioshock.communication.messages.boardgame.MovePlayerOnBoardMessage;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class GameBoardController extends Controller {
  private MessageService clientService;
  private CommunicationClient commClient;
  
  @Inject
  public GameBoardController(Router router, Store store, MessageService clientService, BoardGame game, CommunicationClient commClient) {
    super(store, router, game);
    this.clientService = clientService;
    this.router = router;
    this.commClient = commClient;
  }

  /** Start the game */
  public void show() {
    router.call(Route.FADE_OUT, "mainMenu");
    router.call(Route.START_MUSIC, "boardGame");
    
    Player player = store.getMainPlayer();
    commClient.saveToFile(player.getUsername(), player.getId());
    setScreen(new GameBoardScreen(router, store));
  }

  /** Handles when the server sends the game board to the client */
  public void saveGameBoard(GameBoard gameBoard) {
    store.setGameBoard(gameBoard);
  }

  public void savePlayers(ArrayList<Player> players) {
    store.savePlayers(players);
  }
  
  public void setOwner(UUID[] planetOwner) {
    UUID playerId = planetOwner[0];
    UUID planetId = planetOwner[1];
    
    store.setPlanetOwner(playerId, planetId);
  }
  
  public void updateCoordinates(Coordinates[] coordinates) {
    ArrayList<Player> players = store.getPlayers();
    
    for(int i=0; i<coordinates.length; i++) {
      players.get(i).setCoordinates(coordinates[i]);
    }
  }

  public void move(Coordinates destination) {
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();
    Player mainPlayer = store.getMainPlayer();

    // Initialize path finding
    int gridSize = store.getGameBoard().GRID_SIZE;
    AStarPathfinding pathFinder =
        new AStarPathfinding(
            grid, mainPlayer.getCoordinates(), gridSize, gridSize, store.getPlayers());

    // pathsize - 1 since path includes start position
    ArrayList<Coordinates> path = pathFinder.pathfind(destination);
    float pathCost = (path.size() - 1) * mainPlayer.getFuelGridCost();

    // Handle if player doesn't have enough fuel
    if (mainPlayer.getFuel() < pathCost || pathCost == -10) return;

    // Send move request to the server
    clientService.send(new MovePlayerOnBoardMessage(destination, mainPlayer.getId()));
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

  public void movePlayerToBlackHole(Player player) {
    player.clearBoardMove();
    movePlayerToRandomPoint(player);
  }

  /** Player move received from the server */
  public void moveReceived(MovePlayerOnBoardMessage data) {
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();

    // Get data from the message
    Coordinates goalCoords = data.coordinates;
    Player movingPlayer = store.getPlayer(data.id);

    // Initialize path finding
    int gridSize = store.getGameBoard().GRID_SIZE;
    AStarPathfinding pathFinder =
        new AStarPathfinding(
            grid, movingPlayer.getCoordinates(), gridSize, gridSize, store.getPlayers());

    ArrayList<Coordinates> path = pathFinder.pathfind(goalCoords);
    movingPlayer.createBoardMove(path);
  }

  public void miniGameWon(Player player, Planet planet) {
    // winner gets the planet, previous owner loses it
    if (planet.getPlayerCaptured() != null) {
      Player loser = planet.getPlayerCaptured();
      loser.setPlanetsCaptured(loser.getPlanetsCaptured() - 1);
    }
    planet.setPlayerCaptured(player);
    player.setPlanetsCaptured(player.getPlanetsCaptured() + 1);
    player.setPoints(player.getPoints() + 100);

    if(store.isMainPlayersTurn()) {
      router.call(Route.END_TURN);
    }
  }

  public void miniGameLost(Player player) {
    movePlayerToRandomPoint(player);
    
    if(store.isMainPlayersTurn()) {
      router.call(Route.END_TURN);
    }
  }
  
  public void movePlayerToRandomPoint(Player player) {
    GridPoint[][] grid = store.getGameBoard().getGrid();

    int x, y;
    do {
      x = new Random().nextInt(store.getGameBoard().GRID_SIZE);
      y = new Random().nextInt(store.getGameBoard().GRID_SIZE);
    } while (grid[x][y].getType() != GridPoint.Type.EMPTY);

    player.setCoordinates(new Coordinates(x, y));
  }

  public boolean hasReceivedGrid() {
    return store.getGameBoard().getGrid() != null;
  }

}
