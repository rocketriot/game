package bham.bioshock.client;

import com.badlogic.gdx.Gdx;
import com.google.inject.Inject;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.client.IClientHandler;
import bham.bioshock.communication.messages.AddPlayerMessage;
import bham.bioshock.communication.messages.DisconnectPlayerMessage;
import bham.bioshock.communication.messages.GameBoardMessage;
import bham.bioshock.communication.messages.JoinScreenMoveMessage;
import bham.bioshock.communication.messages.MinigamePlayerMoveMessage;
import bham.bioshock.communication.messages.MinigamePlayerStepMessage;
import bham.bioshock.communication.messages.MinigameStartMessage;
import bham.bioshock.communication.messages.MovePlayerOnBoardMessage;
import bham.bioshock.communication.messages.UpdateObjectiveMessage;

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
            AddPlayerMessage data = (AddPlayerMessage) action.getMessage();
            router.call(Route.ADD_PLAYER, data.players);
            break;
          }
          case REMOVE_PLAYER: {
            DisconnectPlayerMessage data = (DisconnectPlayerMessage) action.getMessage();
            router.call(Route.REMOVE_PLAYER, data.playerId);
            break;
          }
          case START_GAME: {
            router.call(Route.GAME_BOARD_SHOW);
            break;
          }
          case GET_GAME_BOARD: {
            GameBoardMessage data = (GameBoardMessage) action.getMessage();
            
            router.call(Route.GAME_BOARD_SAVE, data.gameBoard);
            router.call(Route.PLAYERS_SAVE, data.players);     
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
            MinigameStartMessage data = (MinigameStartMessage) action.getMessage();
            router.call(Route.START_MINIGAME, data);
            break;
          }
          case MINIGAME_PLAYER_STEP: {
            MinigamePlayerStepMessage data = (MinigamePlayerStepMessage) action.getMessage();
            router.call(Route.MINIGAME_PLAYER_UPDATE, data);
            break;
          }
          case MINIGAME_PLAYER_MOVE: {
            MinigamePlayerMoveMessage data = (MinigamePlayerMoveMessage) action.getMessage();
            router.call(Route.MINIGAME_PLAYER_UPDATE_MOVE, data);
            break;
          }
          case MINIGAME_END: {
            router.call(Route.MINIGAME_END, action.getMessage());
            break;
          }
          case MINIGAME_BULLET: {
            router.call(Route.MINIGAME_BULLET, action.getMessage());
            break;
          }
          case JOIN_SCREEN_MOVE: {
            JoinScreenMoveMessage data = (JoinScreenMoveMessage) action.getMessage();
            router.call(Route.JOIN_SCREEN_UPDATE, data);
            break;
          }
          case MINIGAME_UPDATE_OBJECTIVE: {
            UpdateObjectiveMessage data = (UpdateObjectiveMessage) action.getMessage();
            router.call(Route.MINIGAME_OBJECTIVE_UPDATE, data);
            break;
          }
          default: {
            System.out.println("Received unhandled command: " + action.getCommand().toString());
          }}
      });
  }
}
