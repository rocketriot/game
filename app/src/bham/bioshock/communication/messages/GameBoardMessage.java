package bham.bioshock.communication.messages;

import java.util.ArrayList;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Command;

public class GameBoardMessage extends Message {

  private static final long serialVersionUID = 2846496232087208058L;

  public final ArrayList<Player> cpuPlayers;
  public final Coordinates[] coordinates;
  public final GameBoard gameBoard;
  
  public GameBoardMessage(GameBoard gameBoard, ArrayList<Player> players, ArrayList<Player> cpuPlayers) {
    super(Command.GET_GAME_BOARD);
    coordinates = new Coordinates[4];
    for(int i=0; i<players.size(); i++) {
      coordinates[i] = players.get(i).getCoordinates();
    }
    this.cpuPlayers = cpuPlayers;
    this.gameBoard = gameBoard;
  }
}
