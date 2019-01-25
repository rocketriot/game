package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.common.models.Model;

public class HostScreenController implements Controller {
    private Client client;
    private Model model;

    public HostScreenController(Client client, Model model) {
        this.client = client;
        this.model = model;
    }

    @Override
    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }
}
