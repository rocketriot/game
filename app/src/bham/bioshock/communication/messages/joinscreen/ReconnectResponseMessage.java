package bham.bioshock.communication.messages.joinscreen;

import java.util.ArrayList;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;

public class ReconnectResponseMessage extends Message {

  private static final long serialVersionUID = 306644053577753583L;
  
  public final ArrayList<Player> players;
  public final Coordinates[] coordinates;
  public final GameBoard gameBoard;
  public final boolean isValid;
  public final boolean minigameRunning;
  public final boolean boardgameRunning;
  public final int turnNum;
  public final int roundNum;
  public final int maxRoundsNum;
  
  public ReconnectResponseMessage(Store store) {
    super(Command.RECONNECT_PLAYER);
    this.players = store.getPlayers();  
    this.gameBoard = store.getGameBoard();
    coordinates = new Coordinates[4];
    minigameRunning = store.getMinigameStore() != null;
    turnNum = store.getTurn();
    roundNum = store.getRound();
    maxRoundsNum = store.getMaxRounds();
    isValid = true;
    
    if(gameBoard == null) {
      boardgameRunning = false;
    } else {
      boardgameRunning = true;
      for(int i=0; i<players.size(); i++) {
        coordinates[i] = players.get(i).getCoordinates();
      }      
    }
  }
  
  public ReconnectResponseMessage(boolean isValid) {
    super(Command.RECONNECT_PLAYER);
    this.isValid = false;
    this.players = null;
    this.gameBoard = null;
    this.coordinates = null;
    this.minigameRunning = false;
    this.boardgameRunning = false;
    this.turnNum = 0;
    this.roundNum = 0;
    this.maxRoundsNum = 0;
  }

}
