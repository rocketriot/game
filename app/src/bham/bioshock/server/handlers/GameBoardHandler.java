package bham.bioshock.server.handlers;

import bham.bioshock.common.consts.GridPoint;
import bham.bioshock.common.consts.GridPoint.Type;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.pathfinding.AStarPathfinding;
import java.util.ArrayList;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.boardgame.GameBoardMessage;
import bham.bioshock.communication.messages.boardgame.MovePlayerOnBoardMessage;
import bham.bioshock.communication.messages.boardgame.UpdateTurnMessage;
import bham.bioshock.communication.messages.minigame.RequestMinigameStartMessage;
import bham.bioshock.communication.server.BoardAi;
import bham.bioshock.server.ServerHandler;

import java.util.UUID;

public class GameBoardHandler {

  Store store;
  ServerHandler handler;
  MinigameHandler minigameHandler;

  public GameBoardHandler(Store store, ServerHandler handler,
      MinigameHandler minigameHandler) {
    this.store = store;
    this.handler = handler;
    this.minigameHandler = minigameHandler;
  }
  
  private void generateGrid(GameBoard board, ArrayList<Player> players) {
    // Set coordinates of the players
    int last = board.GRID_SIZE - 1;
    players.get(0).setCoordinates(new Coordinates(0, 0));
    players.get(1).setCoordinates(new Coordinates(0, last));
    players.get(2).setCoordinates(new Coordinates(last, last));
    players.get(3).setCoordinates(new Coordinates(last, 0));

    board.generateGrid();
  }

  /** Adds a player to the server and sends the player to all the clients */
  public void getGameBoard(ArrayList<Player> additionalPlayers) {
    ArrayList<Player> players = store.getPlayers();
    if(additionalPlayers != null) {
      players.addAll(additionalPlayers);
    }
    
    // Generate a grid when starting the game
    GameBoard gameBoard = new GameBoard(); 
    generateGrid(gameBoard, players);

    handler.sendToAll(new GameBoardMessage(gameBoard, players, additionalPlayers));
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
    if (pathCost <= currentPlayer.getFuel() && (goalType.equals(Type.EMPTY) || goalType
        .equals(Type.FUEL) || goalType
            .equals(Type.UPGRADE))) {

      handler.sendToAll(new MovePlayerOnBoardMessage(goalCoords, playerID));

      if (currentPlayer.isCpu()) {
        int waitTime = calculateMoveTime(currentPlayer, path);
        new Thread(() -> {
          try {
            Thread.sleep(waitTime);
            Planet planet;
            if ((planet = gameBoard
                .getAdjacentPlanet(currentPlayer.getCoordinates(), currentPlayer)) != null) {
              startMinigame(gameBoard, currentPlayer, planet, minigameHandler);
            } else {
              endTurn(currentPlayer.getId());
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }).start();
      }
    }
  }

  private void startMinigame(GameBoard gameBoard, Player currentPlayer, Planet planet, MinigameHandler minigameHandler) {
    minigameHandler.startMinigame(new RequestMinigameStartMessage(planet.getId()), currentPlayer.getId(), this);
  }

  private int calculateMoveTime(Player player,
      ArrayList<Coordinates> path) {

    // Players move 3 tiles per second + 500 to prevent race condition
    if (path != null)
      return (path.size() * 1000)/3 + 500;
    else
      return 0;
  }

  public void endTurn(UUID id) {
    handler.sendToAll(new UpdateTurnMessage());
    // Handle if the next player is a CPU
    new Thread(() -> {
      try {
        int waitTime = 100;
        while(store.getMovingPlayer().getId().equals(id)) {
          Thread.sleep(waitTime);
        }

        if (store.getMovingPlayer().isCpu())
          new BoardAi(store, this).run();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();
  }
}
