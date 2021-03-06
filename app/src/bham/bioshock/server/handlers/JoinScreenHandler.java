package bham.bioshock.server.handlers;

import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.interfaces.ServerService;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.joinscreen.*;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JoinScreenHandler {

  private Store store;
  private MultipleConnectionsHandler handler;
  private ConcurrentHashMap<UUID, Long> lastRocketMessage = new ConcurrentHashMap<>();

  public JoinScreenHandler(Store store, MultipleConnectionsHandler handler) {
    this.store = store;
    this.handler = handler;
  }

  /**
   * Registers new player
   *
   * @param action
   * @param service
   */
  public void registerPlayer(Message message, ServerService service) {
    if (message instanceof RegisterMessage) {
      RegisterMessage data = (RegisterMessage) message;
      Player player = data.player;
      handler.register(player.getId(), player.getUsername(), service);
      addPlayer(player);
    } else if (message instanceof ReconnectMessage) {
      ReconnectMessage data = (ReconnectMessage) message;
      Player player = store.getPlayer(data.playerId);
      if (player != null) {
        handler.register(player.getId(), player.getUsername(), service);
      } else {
        service.send(new ReconnectResponseMessage(false));
      }
    }
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

    ArrayList<Player> players = store.getPlayers();
    players.add(player);

    // Send all connected clients the new player
    handler.sendToAllExcept(new AddPlayerMessage(player), player.getId());

    // Send all connected players to the new player
    handler.sendTo(player.getId(), new AddPlayerMessage(players));
  }

  public void disconnectPlayer(UUID clientId) {
    Player player = store.getPlayer(clientId);
    if (player != null) {
      player.setCpu(true);
    }
    handler.sendToAll(new DisconnectPlayerMessage(clientId));
  }

  /**
   * Create CPU players
   *
   * @return list of created cpu players
   */
  private ArrayList<Player> createCpuPlayers() {
    ArrayList<Player> cpuPlayers = new ArrayList<>();
    int storedPlayersNum = store.getPlayers().size();

    // If there is less then 4 players, create new CPU player
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

  /**
   * Creates CPU players and starts the game
   *
   * @param gameBoardHandler
   */
  public void startGame(GameBoardHandler gameBoardHandler) {
    ArrayList<Player> cpuPlayers = createCpuPlayers();

    // Send the board and the players
    gameBoardHandler.getGameBoard(cpuPlayers, true);
    gameBoardHandler.startAI();
  }

  /**
   * Starts minigame directly from the join screen
   *
   * @param playerId initiator
   * @param gameBoardHandler
   * @param minigameHandler
   */
  public void minigameDirectStart(
      UUID playerId, GameBoardHandler gameBoardHandler, MinigameHandler minigameHandler) {
    ArrayList<Player> cpuPlayers = createCpuPlayers();
    // Send the board and the players
    gameBoardHandler.getGameBoard(cpuPlayers, false);

    // Wait for the board
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    minigameHandler.startMinigame(playerId, null, gameBoardHandler, null);
  }

  /**
   * Update rocket position on the board
   *
   * @param message
   * @param playerId
   */
  public void moveRocket(JoinScreenMoveMessage message, UUID playerId) {
    Long lastMessage = lastRocketMessage.get(playerId);

    // Ignore old messages
    if (lastMessage == null || lastMessage <= message.created) {
      lastRocketMessage.put(playerId, message.created);
      handler.sendToAllExcept(message, playerId);
    }
  }

  /**
   * Reconnect
   *
   * @param playerId
   */
  public void sendReconnectData(UUID playerId) {
    Player player = store.getPlayer(playerId);
    player.setCpu(false);
    handler.sendTo(playerId, new ReconnectResponseMessage(store));
  }
}
