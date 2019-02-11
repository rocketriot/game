package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.Client.View;
import bham.bioshock.client.screens.JoinScreen;
import bham.bioshock.common.models.Player;

import java.net.ConnectException;

public class MainMenuController extends Controller {

  public MainMenuController(Client client) {
    this.client = client;
  }

  /** Creates a server and send the player to the join screen */
  public void createServer(String username) {
    client.createHostingServer();

    //get the join screen controller
    JoinScreenController jsc = (JoinScreenController) client.getController(View.JOIN_SCREEN);
    try {
      jsc.connectToServer(username);

      //jsc.startGame();
    } catch (ConnectException e) {
      // Handle connection error
    }
    System.out.println(client.getModel().getPlayers().size());
    changeScreen(View.JOIN_SCREEN);
  }
}
