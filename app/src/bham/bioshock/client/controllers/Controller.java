package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.common.models.Model;


/** Root controller used by all other controllers */
public interface Controller {
    public void changeScreen(Client.View view);

}