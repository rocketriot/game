package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.Client.View;
import bham.bioshock.common.models.Model;

public class HowToController implements Controller {
    private Client client;
    private Model model;

    public HowToController(Client client, Model model) {
        this.client = client;
        this.model = model;
    }

    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }
}