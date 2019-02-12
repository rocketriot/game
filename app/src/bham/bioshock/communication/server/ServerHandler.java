package bham.bioshock.communication.server;

import bham.bioshock.common.models.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.server.handlers.GameBoardHandler;
import bham.bioshock.server.handlers.JoinScreenHandler;

import java.util.ArrayList;
import java.util.UUID;

public class ServerHandler {

  private Store model;
  private ArrayList<ServerService> connections;

  public ServerHandler(Store _model) {
    connections = new ArrayList<>();
    model = _model;
  }

  public void register(ServerService service) {
    connections.add(service);
  }

  public ArrayList<ServerService> getConnections() {
    return connections;
  }

  public void sendToAll(Action action) {
    for (ServerService s : connections) {
      s.send(action);
    }
  }

  public void sendTo(UUID clientId, Action action) {
    // TODO
  }

  public void handleRequest(Action action) {
    try {
      switch (action.getCommand()) {
        case ADD_PLAYER:
          JoinScreenHandler.addPlayer(model, action, this);
          break;
        case START_GAME:
          JoinScreenHandler.startGame(model, action, this);
          break;
        case GET_GAME_BOARD:
          GameBoardHandler.getGameBoard(model, action, this);
          break;
        default:
          System.out.println("Received unhandled command: " + action.getCommand().toString());
          break;
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
