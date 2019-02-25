package bham.bioshock.server.handlers;

import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.Sendable;
import bham.bioshock.communication.server.ServerHandler;
import bham.bioshock.communication.server.ServerService;

import java.io.Serializable;
import java.util.ArrayList;

public class JoinScreenHandler {

  Store store;
  ServerHandler handler;
  public JoinScreenHandler(Store store, ServerHandler handler) {
    this.store = store;
    this.handler = handler;
  }
  
  /**
   * Adds a player to the server and sends the player to all the clients
   *
   * @throws Exception
   */
  public void addPlayer(Action action, ServerService service) throws Exception {
    Player player = (Player) action.getArgument(0);
    if(store.getPlayers().size() >= 4) {
      handler.sendTo(service.Id(), new Action(Command.SERVER_FULL));
      return;
    }

    // Save client's ID
    service.saveId(player.getId());

    // Set the texture ID of the player
    int textureId = store.getPlayers().size();
    player.setTextureID(textureId);

    // Send all connected clients the new player
    handler.sendToAllExcept(action, service.Id());

    // Send all connected players to the new player
    ArrayList<Serializable> arguments = new ArrayList<>();
    for (Player p : store.getPlayers()) {
      arguments.add(p);
    }
    arguments.add(player);
    handler.sendTo(player.getId(), new Action(Command.ADD_PLAYER, arguments));
  }

  public void disconnectPlayer(ServerService service) {
    handler.sendToAll(new Action(Command.REMOVE_PLAYER, service.Id()));
  }

  /** Creates CPU players and starts the game */
  public void startGame(Action action, GameBoardHandler gameBoardHandler) {
    ArrayList<Player> cpuPlayers = new ArrayList<>();
    int storedPlayersNum = store.getPlayers().size();

    // If there is not 4 players, create CPU players
    while (storedPlayersNum + cpuPlayers.size() < store.MAX_PLAYERS) {
      int number = storedPlayersNum + cpuPlayers.size();

      Player player = new Player("Player " + (number+1), true);

      // Set the texture ID of the player
      int textureId = number;
      player.setTextureID(textureId);
      cpuPlayers.add(player);
    }

    // Send the board and the players
    gameBoardHandler.getGameBoard(action, cpuPlayers);
    
    // Tell the clients to start the game
    handler.sendToAll(new Action(Command.START_GAME));
  }
}
