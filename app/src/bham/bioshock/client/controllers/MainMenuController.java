package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;

public class MainMenuController implements Controller {
    private Client client;

    public MainMenuController(Client client) {
        this.client = client;
    }

    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }
}