package bham.bioshock.server.handlers;

import bham.bioshock.common.models.Store;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;
import bham.bioshock.communication.server.ServerService;
import java.io.Serializable;
import java.util.ArrayList;

public class JoinScreenHandler {

  /**
   * Adds a player to the server and sends the player to all the clients
   *
   * @throws Exception
   */
  public static void addPlayer(Store model, Action action, ServerHandler handler, ServerService service) throws Exception {
    Player player = (Player) action.getArgument(0);
    
    // Save client's ID
    service.saveId(player.getId());
    
    // Set the texture ID of the player
    int textureId = model.getPlayers().size();
    player.setTextureID(textureId);

    // Add a player to the model
    model.addPlayer(player);

    // Send all connected clients the new player
    handler.sendToAllExcept(action, service.Id());
    
    // Send all connected players to the new player
    ArrayList<Serializable> arguments = new ArrayList<>();
    for(Player p : model.getPlayers()) {
      arguments.add(p);
    }
    handler.sendTo(player.getId(), new Action(Command.ADD_PLAYER, arguments));


    // If there are the max number of players start the game
    if (model.getPlayers().size() == model.MAX_PLAYERS) {
      System.out.println("starting game...");
      handler.sendToAll(new Action(Command.START_GAME));
    }

  }
  
  public static void disconnectPlayer(Store store, ServerService service, ServerHandler handler) {
    store.removePlayer(service.Id());
    handler.sendToAll(new Action(Command.REMOVE_PLAYER, service.Id()));
  }

  /** Creates CPU players and starts the game */
  public static void startGame(Store store, Action action, ServerHandler handler) {
    ArrayList<Serializable> cpuPlayers = new ArrayList<>();

    // If there is not 4 players, create CPU players
    System.out.println("PLAYERS IN THE STORE: "+store.getPlayers());
    while (store.getPlayers().size() != store.MAX_PLAYERS) {
      int number = store.getPlayers().size();

      Player player = new Player("Player " + number, true);

      // Set the texture ID of the player
      int textureId = store.getPlayers().size();
      player.setTextureID(textureId);

      store.addPlayer(player);
      cpuPlayers.add(player);
    }
    
    // Send the board and the players
    GameBoardHandler.getGameBoard(store, action, handler);
    
    // Tell the clients to start the game
    handler.sendToAll(new Action(Command.START_GAME));
  }
}
