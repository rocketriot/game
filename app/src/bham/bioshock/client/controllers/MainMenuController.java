package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.Client.View;

public class MainMenuController extends Controller {

    public MainMenuController(Client client) {
        this.client = client;
    }

    /** Creates a server and send the player to the join screen */
    public void createServer() {
        client.createHostingServer();

        changeScreen(View.JOIN_SCREEN);
    }
}