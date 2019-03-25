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
  
  public final AddPlayerMessage players;
  public final Coordinates[] coordinates;
  public final GameBoard gameBoard;
  public final boolean minigameRunning;
  
  public ReconnectResponseMessage(Store store) {
    super(Command.RECONNECT_PLAYER);
    ArrayList<Player> players = store.getPlayers();
    this.players = new AddPlayerMessage(players);
    this.gameBoard = store.getGameBoard();
    
    coordinates = new Coordinates[4];
    for(int i=0; i<players.size(); i++) {
      coordinates[i] = players.get(i).getCoordinates();
    }
    minigameRunning = store.getMinigameStore() != null;
  }

}
