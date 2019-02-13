package bham.bioshock.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import com.google.inject.Inject;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.client.IClientHandler;

public class ClientHandler implements IClientHandler {
  
  private Router router;
  
  @Inject
  public ClientHandler(Router router) {
    this.router = router;
  }
  
  public void execute(Action action) {
    switch (action.getCommand()) {
      case ADD_PLAYER: {
        ArrayList<Player> players = new ArrayList<>();
        for(Serializable p : action.getArguments()) {
          players.add((Player) p);
        }
        router.call(Route.ADD_PLAYER, players);
        break;
      }
      case REMOVE_PLAYER: {
        UUID id = (UUID) action.getArgument(0);
        router.call(Route.REMOVE_PLAYER, id);
        break;
      }
      case START_GAME: {
        router.call(Route.START_GAME);
        break;
      }
      case GET_GAME_BOARD: {
        GameBoard gameBoard = (GameBoard) action.getArgument(0);
        router.call(Route.GAME_BOARD_SAVE, gameBoard);
        break;
      }
      default: {
        System.out.println("Received unhandled command: " + action.getCommand().toString());
      }
    }
  }
}
