package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.Client.View;
import bham.bioshock.common.models.Model;

public class MainMenuController implements Controller {
    private Client client;
    private Model model;

    public MainMenuController(Client client, Model model) {
        this.client = client;
        this.model = model;
    }
}