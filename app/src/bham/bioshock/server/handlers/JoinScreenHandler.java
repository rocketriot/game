package bham.bioshock.server.handlers;

import bham.bioshock.common.models.Model;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;

import java.io.Serializable;
import java.util.ArrayList;

public class JoinScreenHandler {

  /**
   * Adds a player to the server and sends the player to all the clients
   *
   * @throws Exception
   */
  public static void addPlayer(Model model, Action action, ServerHandler hander) throws Exception {
    Player player = (Player) action.getArgument(0);

    // Set the texture ID of the player
    int textureId = model.getPlayers().size();
    player.setTextureID(textureId);

    // Add a player to the model
    model.addPlayer(player);

    // Send add player action to all clients
    hander.sendToAll(action);

    // If there are the max number of players start the game
    if (model.getPlayers().size() == model.MAX_PLAYERS) {
      hander.sendToAll(new Action(Command.START_GAME));
    }
  }

  /** Creates CPU players and starts the game */
  public static void startGame(Model model, Action action, ServerHandler hander) {
    ArrayList<Serializable> cpuPlayers = new ArrayList<>();

    // If there is not 4 players, create CPU players
    while (model.getPlayers().size() != model.MAX_PLAYERS) {
      int number = model.getPlayers().size();

      Player player = new Player("Player " + number, true);

      // Set the texture ID of the player
      int textureId = model.getPlayers().size();
      player.setTextureID(textureId);

      model.addPlayer(player);
      cpuPlayers.add(player);
    }

    // Send new CPU players to all clients
    hander.sendToAll(new Action(Command.ADD_PLAYER, cpuPlayers));

    // Tell the clients to start the game
    hander.sendToAll(new Action(Command.START_GAME));
  }
}
