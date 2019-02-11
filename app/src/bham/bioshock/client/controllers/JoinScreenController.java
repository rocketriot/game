package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.Client.View;
import bham.bioshock.client.screens.JoinScreen;
import bham.bioshock.common.models.Model;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.client.CommunicationClient;
import com.badlogic.gdx.Screen;

import java.io.Serializable;
import java.net.ConnectException;
import java.util.ArrayList;

public class JoinScreenController extends Controller {
  private Client client;
  private Model model;
  private ClientService server;

  public JoinScreenController(Client client) {
    this.client = client;
    this.model = client.getModel();
    this.server = client.getServer();
  }

  /** Create a connection with the server and wait in lobby when a username is entered */
  public void connectToServer(String username) throws ConnectException {
    // Create server connection
    server = CommunicationClient.connect(username, client);

    client.setServer(server);

    // Create a new player
    Player player = new Player(username);
    model.setMainPlayer(player);

    // Add the player to the server
    server.send(new Action(Command.ADD_PLAYER, player));
  }

  /** Handle when the server tells us a new player was added to the game */
  public void onPlayerJoined(Action action) {
    for (Serializable argument : action.getArguments()) {
      Player player = (Player) argument;
      model.addPlayer(player);
      System.out.println("Player: " + player.getUsername() + " connected");
    }
  }

  /** Fetches players from the model */
  public ArrayList<Player> getPlayers() {
    return model.getPlayers();
  }

  /** Tells the server to start the game */
  public void startGame() {
    server.send(new Action(Command.START_GAME));
  }

  /** Handle when the server tells the client to start the game */
  public void onStartGame(Action action) {
    System.out.println("Ready to start!");
    client.changeScreen(Client.View.GAME_BOARD);
  }

  public void setScreen(Screen screen) {
    this.screen = (JoinScreen) screen;
  }

  @Override
  public void changeScreen(View view) {
    client.changeScreen(view);
  }
}
