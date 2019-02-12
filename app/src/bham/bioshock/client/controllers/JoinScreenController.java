package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.Store;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.client.CommunicationClient;
import com.google.inject.Inject;
import java.io.Serializable;
import java.net.ConnectException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JoinScreenController extends Controller {

  private static final Logger logger = LogManager.getLogger(JoinScreenController.class);

  private ClientService clientService;
  private CommunicationClient commClient;

  @Inject
  public JoinScreenController(Store store, Router router, CommunicationClient commClient) {
    super(store, router);
    this.commClient = commClient;
  }

  public void show() {
    // TODO: Display screen for providing username

    // Create a new player
    Player player = new Player("Test player");
    store.setMainPlayer(player);
    store.addPlayer(player);
    Player p = store.getMainPlayer();

    // This will be called from the screen
    router.call(Route.CLIENT_CONNECT);
  }

  public void connect() {
    Player p = store.getMainPlayer();
    try {
      connectToServer(p);
    } catch (ConnectException e) {
      logger.debug("Can't connect to the server");
    }
    
    router.call(Route.GAME_BOARD);
  }

  /** Create a connection with the server and wait in lobby when a username is entered */
  public void connectToServer(Player player) throws ConnectException {
    // Create server connection
    clientService = commClient.connect(player.getUsername());

    // Add the player to the server
    clientService.send(new Action(Command.ADD_PLAYER, player));
  }

  /** Handle when the server tells us a new player was added to the game */
  public void onPlayerJoined(Action action) {
    for (Serializable argument : action.getArguments()) {
      Player player = (Player) argument;
      store.addPlayer(player);
      logger.debug("Player: " + player.getUsername() + " connected");
      //change the label
      updateLabels(player);
    }
  }

  /** Tells the server to start the game */
  public void startGame() {
    clientService.send(new Action(Command.START_GAME));
  }

  /** Handle when the server tells the client to start the game */
  public void onStartGame(Action action) {
    logger.debug("Ready to start!");
    router.call(Route.GAME_BOARD);
  }

  private void updateLabels(Player player) {
      //get the number of players
    int num_players = getPlayers().size() - 1;
    JoinScreen js = (JoinScreen) client.getScreen(View.JOIN_SCREEN);
    js.changePlayerName(js.getPlayer_names().get(num_players), player.getUsername());
    js.changeWaitLabel(js.getWaiting_labels().get(num_players), JoinScreen.WaitText.CONNECTED);
  }
}
