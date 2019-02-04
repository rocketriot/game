package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;

/** Root controller used by all other controllers */
public interface Controller {
    public void changeScreen(Client.View view);

}