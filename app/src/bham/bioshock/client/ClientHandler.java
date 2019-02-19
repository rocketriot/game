package bham.bioshock.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import com.google.inject.Inject;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.client.IClientHandler;
import com.badlogic.gdx.Gdx;

public class ClientHandler implements IClientHandler {
  
  private Router router;
  
  @Inject
  public ClientHandler(Router router) {
    this.router = router;
  }
  
  public void execute(Action action) {
    Gdx.app.postRunnable(
      () -> {
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
            router.call(Route.GAME_BOARD_SHOW);
            break;
          }
          case GET_GAME_BOARD: {
            ArrayList<Serializable> arguments = action.getArguments();
            ArrayList<Player> players = new ArrayList<>();
            Iterator<Serializable> itr = arguments.iterator();
            int i = 0;
            
            while(itr.hasNext()) {
              if(i == 0) {
                GameBoard gameBoard = (GameBoard) itr.next();
                router.call(Route.GAME_BOARD_SAVE, gameBoard);
              } else {
                Player player = (Player) itr.next();
                players.add(player);
              }
              i++;
            }

            router.call(Route.PLAYERS_SAVE, players);     
            break;
          }
          case MOVE_PLAYER_ON_BOARD: {
            ArrayList<Serializable> arguments = action.getArguments();
            GameBoard gameBoard = (GameBoard) arguments.get(0);
            Player movingPlayer = (Player) arguments.get(1);

            router.call(Route.MOVE_RECEIVED, movingPlayer);
            router.call(Route.GAME_BOARD_SAVE, gameBoard);
            break;
          }
          case MINIGAME_START: {
            router.call(Route.START_MINIGAME);
            break;
          }
          case MINIGAME_PLAYER_MOVE: {
            router.call(Route.MINIGAME_PLAYER_UPDATE, action.getArguments());
            break;
          }
          case MINIGAME_END: {
            router.call(Route.MINIGAME_END, action.getArguments());
            break;
          }
          default: {
            System.out.println("Received unhandled command: " + action.getCommand().toString());
          }}
      });
  }
}
