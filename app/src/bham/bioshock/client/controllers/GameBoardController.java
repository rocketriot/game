package bham.bioshock.client.controllers;

import bham.bioshock.client.Router;
import bham.bioshock.client.screens.GameBoardScreen;
import bham.bioshock.client.BoardGame;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
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
  public GameBoardController(Router router, Store store, IClientService clientService,
      BoardGame game) {
    super(store, router, game);
    this.clientService = clientService;
    this.router = router;
  }

  public void show() {
    // If the grid is not yet loaded, go to loading screen and fetch the game board
    // from the server
    if (!hasReceivedGrid()) {
      clientService.send(new Action(Command.GET_GAME_BOARD));
      router.call(Route.LOADING);
    }
  }

  /** Handles when the server sends the game board to the client */
  public void saveGameBoard(GameBoard gameBoard) {
    store.setGameBoard(gameBoard);

    setScreen(new GameBoardScreen(router, store, gameBoard));
    router.call(Route.GAME_BOARD);
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
    AStarPathfinding pathFinder = new AStarPathfinding(grid, mainPlayer.getCoordinates(), gridSize, gridSize);
    pathFinder.setStartPosition(mainPlayer.getCoordinates());

    // pathsize - 1 since path includes start position
    ArrayList<Coordinates> path = pathFinder.pathfind(destination);
    float pathCost = (path.size() - 1) * 10;

    // Handle if player doesn't have enough fuel
    if (mainPlayer.getFuel() < pathCost) return;

    // Update player coordinates and fuel
    mainPlayer.setCoordinates(destination);
    mainPlayer.decreaseFuel(pathCost);

    // Get grid point the user landed on
    GridPoint gridPoint = gameBoard.getGridPoint(destination);

    // Check if the player landed on a fuel box
    if (gridPoint.getType() == GridPoint.Type.FUEL) {
      // Decrease players amount of fuel
      Fuel fuel = (Fuel) gridPoint.getValue();
      mainPlayer.decreaseFuel(fuel.getValue());
    }

    // Send the updated grid to the server
    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add(gameBoard);
    arguments.add(mainPlayer);
    clientService.send(new Action(Command.MOVE_PLAYER_ON_BOARD, arguments));
  }

  /** Player move received from the server */
  public void moveReceived(Action action) {
    // Get game board and player from arguments and update the model
    GameBoard gameBoard = (GameBoard) action.getArgument(0);
    Player movingPlayer = (Player) action.getArgument(1);
    store.setGameBoard(gameBoard);
    store.updatePlayer(movingPlayer);

    store.nextTurn();
  }

  public void startMinigame() {}

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

  /** Check if it is the main player's turn */
  public boolean isMainPlayersTurn() {
    int turn = store.getTurn();
    Player nextPlayer = store.getPlayers().get(turn);

    return store.getMainPlayer().getId().equals(nextPlayer.getId());
  }
}
