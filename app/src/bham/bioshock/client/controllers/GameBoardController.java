package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.GameBoardScreen;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.IClientService;
import com.google.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class GameBoardController extends Controller {
  private IClientService clientService;

  @Inject
  public GameBoardController(Router router, Store store, IClientService clientService, BoardGame game) {
    super(store, router, game);
    this.clientService = clientService;
    this.router = router;
  }

  /** Start the game */
  public void show() {
    router.call(Route.FADE_OUT, "mainMenu");
    router.call(Route.START_MUSIC, "boardGame");
    setScreen(new GameBoardScreen(router, store));
  }

  /** Handles when the server sends the game board to the client */
  public void saveGameBoard(GameBoard gameBoard) {
    store.setGameBoard(gameBoard);
  }

  public void savePlayers(ArrayList<Player> players) {
    store.setPlayers(players);
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
    pathFinder.setStartPosition(mainPlayer.getCoordinates());

    // pathsize - 1 since path includes start position
    ArrayList<Coordinates> path = pathFinder.pathfind(destination);
    float pathCost = (path.size() - 1) * 10;

    // Handle if player doesn't have enough fuel
    if (mainPlayer.getFuel() < pathCost || pathCost == -10) return;

    // Get grid point the user landed on
    GridPoint gridPoint = gameBoard.getGridPoint(destination);

    // Check if the player landed on a fuel box
    if (gridPoint.isType(GridPoint.Type.FUEL)) {
      // Decrease players amount of fuel
      Fuel fuel = (Fuel) gridPoint.getValue();
      mainPlayer.increaseFuel(fuel.getValue());
    }

    mainPlayer.createBoardMove(path);
    mainPlayer.setCoordinates(destination);
    mainPlayer.decreaseFuel(pathCost);

    // Send the updated grid to the server
    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add(gameBoard);
    arguments.add(mainPlayer);
    clientService.send(new Action(Command.MOVE_PLAYER_ON_BOARD, arguments));
  }

  /** Ends the players turn */
  public void endTurn() {
    clientService.send(new Action(Command.END_TURN));
  }

  /** Player move received from the server */
  public void moveReceived(Player movingPlayer) {
    // Update the model
    Player p = store.getPlayer(movingPlayer.getId());
    p.setCoordinates(movingPlayer.getCoordinates());
    p.setFuel(movingPlayer.getFuel());
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
  }

  public void miniGameLost(Player player) {
    GridPoint[][] grid = store.getGameBoard().getGrid();

    // if player attacks planet and doesn't win gets moved in a random position
    int x, y;
    do {
      x = new Random().nextInt();
      y = new Random().nextInt();
    } while (grid[x][y].getType() != GridPoint.Type.EMPTY);

    Coordinates newCoordinates = new Coordinates(x, y);
    player.setCoordinates(newCoordinates);
  }

  public boolean hasReceivedGrid() {
    return store.getGameBoard().getGrid() != null;
  }

}
