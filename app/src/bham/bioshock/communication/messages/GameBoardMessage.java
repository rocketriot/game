package bham.bioshock.communication.messages;

import java.util.ArrayList;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Command;

public class GameBoardMessage extends Message {

  private static final long serialVersionUID = 2846496232087208058L;

  private final ArrayList<Player> players;
  private final GameBoard gameBoard;
  
  public GameBoardMessage(GameBoard gameBoard, ArrayList<Player> players) {
    super(Command.GET_GAME_BOARD);
    this.players = players;
    this.gameBoard = gameBoard;
  }

  public GameBoard getGameBoard() {
    return gameBoard;
  }

  public ArrayList<Player> getPlayers() {
    return players;
  }

}
