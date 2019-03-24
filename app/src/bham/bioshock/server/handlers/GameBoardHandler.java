package bham.bioshock.server.handlers;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import java.util.ArrayList;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.boardgame.GameBoardMessage;
import bham.bioshock.communication.messages.boardgame.MovePlayerOnBoardMessage;
import bham.bioshock.communication.messages.boardgame.UpdateTurnMessage;
import bham.bioshock.communication.server.BoardAi;
import bham.bioshock.server.ServerHandler;

import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameBoardHandler {
  
  private static final Logger logger = LogManager.getLogger(GameBoardHandler.class);
  
  Store store;
  ServerHandler handler;
  MinigameHandler minigameHandler;
  BoardAi boardAi;

  public GameBoardHandler(Store store, ServerHandler handler,
      MinigameHandler minigameHandler) {
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
    if(additionalPlayers != null) {
      players.addAll(additionalPlayers);
    }
    
    // Generate a grid when starting the game
    GameBoard gameBoard = new GameBoard(); 
    generateGrid(gameBoard, players);

    handler.sendToAll(new GameBoardMessage(gameBoard, players, additionalPlayers, startGame));
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
        new AStarPathfinding(
            grid, startCoords, gridSize, gridSize, store.getPlayers());

    ArrayList<Coordinates> path = pathFinder.pathfind(goalCoords);
    float pathCost = (path.size() - 1) * 10;

    GridPoint.Type goalType = gameBoard.getGridPoint(goalCoords).getType();
    
    
    if (pathCost <= currentPlayer.getFuel() && goalType.isValidForPlayer()) {
      handler.sendToAll(new MovePlayerOnBoardMessage(goalCoords, playerID));
    }
  }

  public void endTurn() {
    handler.sendToAll(new UpdateTurnMessage());
  }

}
