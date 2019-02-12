package bham.bioshock.client;

import com.badlogic.gdx.Gdx;
import bham.bioshock.communication.Action;

public class ClientHandler {
  
  public void handleServerMessages(Action action) {
    Gdx.app.postRunnable(
        () -> {
//          switch (action.getCommand()) {
//            case ADD_PLAYER:
//              {
//                JoinScreenController controller =
//                    (JoinScreenController) controllers.get(View.JOIN_SCREEN);
//                controller.onPlayerJoined(action);
//                break;
//              }
//            case START_GAME:
//              {
//                JoinScreenController controller =
//                    (JoinScreenController) controllers.get(View.JOIN_SCREEN);
//                controller.onStartGame(action);
//                break;
//              }
//            case GET_GAME_BOARD:
//              {
//                GameBoardController controller =
//                    (GameBoardController) controllers.get(View.GAME_BOARD);
//                controller.gameBoardReceived(action);
//                break;
//              }
//            default:
//              {
//                System.out.println("Received unhandled command: " + action.getCommand().toString());
//              }
//          }
        });
  }
}
