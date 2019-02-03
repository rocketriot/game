package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;

public class HowToController implements Controller {
    private Client client;

    public HowToController(Client client) {
        this.client = client;
    }

    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }
}