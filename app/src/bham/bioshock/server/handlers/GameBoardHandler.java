package bham.bioshock.server.handlers;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.consts.GridPoint.Type;
import bham.bioshock.common.models.BlackHole;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.boardgame.*;
import bham.bioshock.server.ai.BoardAi;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class GameBoardHandler {

  private static final Logger logger = LogManager.getLogger(GameBoardHandler.class);

  Store store;
  MultipleConnectionsHandler handler;
  MinigameHandler minigameHandler;
  BoardAi boardAi;

  public GameBoardHandler(
      Store store, MultipleConnectionsHandler handler, MinigameHandler minigameHandler) {
    this.store = store;
    this.handler = handler;
    this.minigameHandler = minigameHandler;
    this.boardAi = new BoardAi(store, this, minigameHandler);
  }

  private void generateGrid(GameBoard board, ArrayList<Player> players) {
    // Set coordinates of the players
    int last = board.GRID_SIZE - 1;
    players.get(0).setSpawnPoint(new Coordinates(0, 0));
    players.get(1).setSpawnPoint(new Coordinates(0, last));
    players.get(2).setSpawnPoint(new Coordinates(last, last));
    players.get(3).setSpawnPoint(new Coordinates(last, 0));

    for (Player p : players) {
      p.moveToSpawn();
    }

    board.generateGrid();
  }

  public void startAI() {
    boardAi.start();
  }

  /** Adds a player to the server and sends the player to all the clients */
  public void getGameBoard(ArrayList<Player> additionalPlayers, boolean startGame) {
    ArrayList<Player> players = store.getPlayers();
    if (additionalPlayers != null) {
      players.addAll(additionalPlayers);
    }

    // Generate a grid when starting the game
    GameBoard gameBoard = new GameBoard();
    generateGrid(gameBoard, players);

    handler.sendToAll(
        new GameBoardMessage(
            gameBoard, players, additionalPlayers, startGame, store.getMaxRounds()));
  }

  /** Handles a player moving on their turn */
  public void movePlayer(Message message, UUID playerID) {
    // Get the goal coordinates of the move
    MovePlayerOnBoardMessage data = (MovePlayerOnBoardMessage) message;
    Coordinates goalCoords = data.coordinates;

    Player currentPlayer = store.getPlayer(playerID);
    Coordinates startCoords = currentPlayer.getCoordinates();

    // Initialise pathfinder
    GameBoard gameBoard = store.getGameBoard();
    GridPoint[][] grid = gameBoard.getGrid();
    int gridSize = store.getGameBoard().GRID_SIZE;
    AStarPathfinding pathFinder =
        new AStarPathfinding(grid, startCoords, gridSize, gridSize, store.getPlayers());

    ArrayList<Coordinates> path = pathFinder.pathfind(goalCoords);
    Coordinates randomCoords = null;
    for (Coordinates p : path) {
      if (grid[p.getX()][p.getY()].getType().equals(Type.BLACKHOLE)) {
        randomCoords = getRanCoords(grid);
      }
    }
    float pathCost = (path.size() - 1) * currentPlayer.getFuelGridCost();

    GridPoint.Type goalType = gameBoard.getGridPoint(goalCoords).getType();

    if (pathCost <= currentPlayer.getFuel() && goalType.isValidForPlayer()) {
      handler.sendToAll(new MovePlayerOnBoardMessage(goalCoords, playerID, randomCoords));
    }
  }

  private Coordinates getRanCoords(GridPoint[][] grid) {
    int x, y;
    do {
      x = new Random().nextInt(store.getGameBoard().GRID_SIZE);
      y = new Random().nextInt(store.getGameBoard().GRID_SIZE);
    } while (grid[x][y].getType() != GridPoint.Type.EMPTY);
    return new Coordinates(x, y);
  }

  public void endTurn() {
    logger.debug("Turn ended");
    handler.sendToAll(new UpdateTurnMessage());

    if ((store.getTurn() + 1) == 4 && store.getRound() == store.getMaxRounds()) {
      handler.sendToAll(new EndGameMessage());
    }
  }

  public void addBlackHole(Coordinates coordinates) {
    GameBoard gameBoard = store.getGameBoard();
    gameBoard.addBlackHole(new BlackHole(coordinates));

    handler.sendToAll(new AddBlackHoleMessage(coordinates));
  }
}
