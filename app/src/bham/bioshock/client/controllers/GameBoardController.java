package bham.bioshock.client.controllers;

import bham.bioshock.client.Router;
import bham.bioshock.client.screens.GameBoardScreen;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
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
  private BoardGame game;
  private Router router;
  private GameBoard gameBoard;
  private Player mainPlayer;
  private AStarPathfinding pathFinder;

  @Inject
  public GameBoardController(Router router, Store store, IClientService clientService, BoardGame game) {
    super(store, router);
    this.clientService = clientService;
    this.router = router;
    this.game = game;
    gameBoard = store.getGameBoard();
    mainPlayer = store.getMainPlayer();
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

    pathFinder = new AStarPathfinding(gameBoard.getGrid(), mainPlayer.getCoordinates(), 36, 36);

    game.setScreen(new GameBoardScreen(router, store, gameBoard));
    router.call(Route.GAME_BOARD);
  }

  public void savePlayers(ArrayList<Player> players) {
    // TODO: remove temporary solution to fix coordinates not being sent by the server
    int last = gameBoard.GRID_SIZE - 1;
    players.get(0).setCoordinates(new Coordinates(0, 0));
    players.get(1).setCoordinates(new Coordinates(0, last));
    players.get(2).setCoordinates(new Coordinates(last, last));
    players.get(3).setCoordinates(new Coordinates(last, 0));

    store.setPlayers(players);
  }


  public AStarPathfinding getPathFinder() {
    pathFinder.setStartPosition(mainPlayer.getCoordinates());
    return pathFinder;
  }

  public void move(Coordinates destination) {
    GridPoint[][] grid = gameBoard.getGrid();

    pathFinder.setStartPosition(mainPlayer.getCoordinates());

    // pathsize - 1 since path includes start position
    ArrayList<Coordinates> path = pathFinder.pathfind(destination);
    float pathCost = (path.size() - 1) * 10;

    // check if the player has enough fuel
    if (mainPlayer.getFuel() >= pathCost) {
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
    GridPoint[][] grid = gameBoard.getGrid();

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
    return gameBoard.getGrid() != null;
  }

  /** Check if it is the main player's turn */
  public boolean isMainPlayersTurn() {
    int turn = store.getTurn();
    Player nextPlayer = store.getPlayers().get(turn);

    return store.getMainPlayer().getId().equals(nextPlayer.getId());
  }
}
