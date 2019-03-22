package bham.bioshock.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.badlogic.gdx.Gdx;
import com.google.inject.Inject;
import bham.bioshock.communication.client.IClientHandler;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.boardgame.GameBoardMessage;
import bham.bioshock.communication.messages.boardgame.MovePlayerOnBoardMessage;
import bham.bioshock.communication.messages.joinscreen.AddPlayerMessage;
import bham.bioshock.communication.messages.joinscreen.DisconnectPlayerMessage;
import bham.bioshock.communication.messages.joinscreen.JoinScreenMoveMessage;
import bham.bioshock.communication.messages.minigame.MinigamePlayerMoveMessage;
import bham.bioshock.communication.messages.minigame.MinigamePlayerStepMessage;
import bham.bioshock.communication.messages.minigame.MinigameStartMessage;

public class ClientHandler implements IClientHandler {
  
  private static final Logger logger = LogManager.getLogger(ClientHandler.class);
  private Router router;
  
  @Inject
  public ClientHandler(Router router) {
    this.router = router;
  }
  
  public void execute(Message message) {
    Gdx.app.postRunnable(
      () -> {
        switch (message.command) {
          case ADD_PLAYER: {
            AddPlayerMessage data = (AddPlayerMessage) message;
            router.call(Route.ADD_PLAYER, data.players);
            break;
          }
          case REMOVE_PLAYER: {
            DisconnectPlayerMessage data = (DisconnectPlayerMessage) message;
            router.call(Route.REMOVE_PLAYER, data.playerId);
            break;
          }
          case START_GAME: {
            router.call(Route.GAME_BOARD_SHOW);
            break;
          }
          case GET_GAME_BOARD: {
            GameBoardMessage data = (GameBoardMessage) message;
            
            router.call(Route.PLAYERS_SAVE, data.cpuPlayers);  
            router.call(Route.COORDINATES_SAVE, data.coordinates);  
            router.call(Route.GAME_BOARD_SAVE, data.gameBoard);
            break;
          }
          case MOVE_PLAYER_ON_BOARD: {
            MovePlayerOnBoardMessage m = (MovePlayerOnBoardMessage) message;
            router.call(Route.MOVE_RECEIVED, m);
            break;
          }
          case UPDATE_TURN: {
            router.call(Route.UPDATE_TURN);
            break;
          }
          case MINIGAME_START: {
            MinigameStartMessage data = (MinigameStartMessage) message;
            router.call(Route.START_MINIGAME, data);
            break;
          }
          case MINIGAME_PLAYER_STEP: {
            MinigamePlayerStepMessage data = (MinigamePlayerStepMessage) message;
            router.call(Route.MINIGAME_PLAYER_UPDATE, data);
            break;
          }
          case MINIGAME_PLAYER_MOVE: {
            MinigamePlayerMoveMessage data = (MinigamePlayerMoveMessage) message;
            router.call(Route.MINIGAME_PLAYER_UPDATE_MOVE, data);
            break;
          }
          case MINIGAME_END: {
            router.call(Route.MINIGAME_END, message);
            break;
          }
          case MINIGAME_BULLET: {
            router.call(Route.MINIGAME_BULLET, message);
            break;
          }
          case JOIN_SCREEN_MOVE: {
            JoinScreenMoveMessage data = (JoinScreenMoveMessage) message;
            router.call(Route.JOIN_SCREEN_UPDATE, data);
            break;
          }
          case MINIGAME_OBJECTIVE: {
            router.call(Route.OBJECTIVE_UPDATE, message);
            break;
          }
          default: {
            logger.error("Received unhandled command: " + message.command.toString());
          }}
      });
  }
}
