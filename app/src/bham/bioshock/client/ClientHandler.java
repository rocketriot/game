package bham.bioshock.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import com.badlogic.gdx.Gdx;
import com.google.inject.Inject;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.client.IClientHandler;
import bham.bioshock.communication.messages.GameBoardMessage;
import bham.bioshock.communication.messages.MovePlayerOnBoardMessage;

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
            GameBoardMessage data = (GameBoardMessage) action.getMessage();
            GameBoard gameBoard = data.getGameBoard();
            ArrayList<Player> players = data.getPlayers();
            
            router.call(Route.GAME_BOARD_SAVE, gameBoard);
            router.call(Route.PLAYERS_SAVE, players);     
            break;
          }
          case MOVE_PLAYER_ON_BOARD: {
            MovePlayerOnBoardMessage m = (MovePlayerOnBoardMessage) action.getMessage();
            router.call(Route.MOVE_RECEIVED, m);
            break;
          }
          case UPDATE_TURN: {
            router.call(Route.UPDATE_TURN);
            break;
          }
          case MINIGAME_START: {
            router.call(Route.START_MINIGAME, action.getArguments());
            break;
          }
          case MINIGAME_PLAYER_MOVE: {
            router.call(Route.MINIGAME_PLAYER_UPDATE, action);
            break;
          }
          case MINIGAME_END: {
            router.call(Route.MINIGAME_END, action.getMessage());
            break;
          }
          case MINIGAME_BULLET: {
            router.call(Route.MINIGAME_BULLET, action.getArguments());
            break;
          }
          case JOIN_SCREEN_MOVE: {
            router.call(Route.JOIN_SCREEN_UPDATE, action.getArguments());
            break;
          }
          case SET_PLANET_OWNER: {
            router.call(Route.SET_PLANET_OWNER, action.getArgument(0));
            break;
          }
          default: {
            System.out.println("Received unhandled command: " + action.getCommand().toString());
          }}
      });
  }
}
