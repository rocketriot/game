package bham.bioshock.client.controllers;

import bham.bioshock.client.Router;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.*;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.IClientService;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Random;
import static bham.bioshock.common.consts.GridPoint.Type.*;

public class GameBoardController extends Controller {

  private IClientService clientService;
  private Store store;
  private Router router;
  private GameBoard gameBoard;
  private Player mainPlayer;
  private AStarPathfinding pathFinder;
  private boolean receivedGrid = false;

  @Inject
  public GameBoardController(Router router, Store store, IClientService clientService, BoardGame game) {
    super(store, router, game);
    this.clientService = clientService;
    this.router = router;
    gameBoard = store.getGameBoard();
  }
  
  public void start() {
    
  }
  
  public void saveGameBoard(GameBoard gameBoard) {
    store.setGameBoard(gameBoard);
  }
  
  public void show() {
    // If the grid is not yet loaded, go to loading screen and fetch the game board
    // from the server
    if (receivedGrid == false) {
      router.call(Route.LOADING);
      clientService.send(new Action(Command.GET_GAME_BOARD));
    } else {

    }
  }

  /** Handles when the server sends the game board to the client */
  public void gameBoardReceived(Action action) {
    // Update gameboard from arguments
    gameBoard = (GameBoard) action.getArgument(0);

    receivedGrid = true;

    // TODO change to client's player
    // setMainPlayer(store.getPlayers().get(0));
    pathFinder = new AStarPathfinding(gameBoard.getGrid(), mainPlayer.getCoordinates(), 36, 36);
    // router.changeScreen(View.GAME_BOARD);
  }


  public AStarPathfinding getPathFinder() {
    pathFinder.setStartPosition(mainPlayer.getCoordinates());
    return pathFinder;
  }

  public void move(Coordinates destination) {
    float fuel = mainPlayer.getFuel();
    GridPoint[][] grid = gameBoard.getGrid();
    ArrayList<Coordinates> path = pathFinder.pathfind(destination);

    // pathsize - 1 since path includes start position
    float pathCost = (path.size() - 1) * 10;

    // check if the player has enough fuel
    if (mainPlayer.getFuel() >= pathCost) {
      mainPlayer.setCoordinates(destination);
      fuel -= pathCost;
      mainPlayer.setFuel(fuel);

      int x = destination.getX();
      int y = destination.getY();
      if (grid[x][y].getType() == PLANET)
        startMinigame();
      else if (grid[x][y].getType() == FUEL)
        mainPlayer.setFuel(fuel + 30);
      pathFinder.setStartPosition(mainPlayer.getCoordinates());
    }
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
    } while (grid[x][y].getType() != EMPTY);

    Coordinates newCoordinates = new Coordinates(x, y);
    player.setCoordinates(newCoordinates);
  }

  public boolean hasReceivedGrid() {
    return receivedGrid;
  }
}
