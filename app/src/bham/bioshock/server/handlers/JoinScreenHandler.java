package bham.bioshock.server.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerService;
import bham.bioshock.server.ServerHandler;

public class JoinScreenHandler {

  Store store;
  ServerHandler handler;

  public JoinScreenHandler(Store store, ServerHandler handler) {
    this.store = store;
    this.handler = handler;
  }

  /**
   * Registers new player
   * 
   * @param action
   * @param service
   */
  public void registerPlayer(Action action, ServerService service) {
    if (store.getPlayers().size() >= 4) {
      service.send(new Action(Command.SERVER_FULL));
      return;
    }
    Player player = (Player) action.getArgument(0);
    handler.registerClient(player.getId(), service);
    addPlayer(player);
  }

  /**
   * Adds a player to the server and sends the player to all the clients
   *
   * @throws Exception
   */
  private void addPlayer(Player player) {
    // Set the texture ID of the player
    int textureId = store.getPlayers().size();
    player.setTextureID(textureId);

    // Send all connected clients the new player
    handler.sendToAllExcept(new Action(Command.ADD_PLAYER, player), player.getId());

    // Send all connected players to the new player
    ArrayList<Serializable> arguments = new ArrayList<>();
    for (Player p : store.getPlayers()) {
      arguments.add(p);
    }
    arguments.add(player);
    handler.sendTo(player.getId(), new Action(Command.ADD_PLAYER, arguments));
  }

  public void disconnectPlayer(UUID clientId) {
    handler.sendToAll(new Action(Command.REMOVE_PLAYER, clientId));
  }

  private ArrayList<Player> createCpuPlayers() {
    ArrayList<Player> cpuPlayers = new ArrayList<>();
    int storedPlayersNum = store.getPlayers().size();

    // If there is not 4 players, create CPU players

    while (storedPlayersNum + cpuPlayers.size() < store.MAX_PLAYERS) {
      int number = storedPlayersNum + cpuPlayers.size();


      Player player = new Player("Player " + (number + 1), true);

      // Set the texture ID of the player
      int textureId = number;
      player.setTextureID(textureId);
      cpuPlayers.add(player);
    }

    return cpuPlayers;
  }

  /** Creates CPU players and starts the game */
  public void startGame(Action action, GameBoardHandler gameBoardHandler) {
    ArrayList<Player> cpuPlayers = createCpuPlayers();
    // Send the board and the players
    gameBoardHandler.getGameBoard(action, cpuPlayers);

    // Tell the clients to start the game
    handler.sendToAll(new Action(Command.START_GAME));
  }

  public void minigameDirectStart(Action action, UUID playerId, GameBoardHandler gameBoardHandler,
      MinigameHandler minigameHandler) {
    ArrayList<Player> cpuPlayers = createCpuPlayers();
    // Send the board and the players
    gameBoardHandler.getGameBoard(action, cpuPlayers);

    // Wait for the board
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    minigameHandler.startMinigame(new Action(Command.MINIGAME_START), playerId, gameBoardHandler);
  }

  public void moveRocket(Action action, UUID player) {
    handler.sendToAllExcept(action, player);
  }

}
