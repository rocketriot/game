package bham.bioshock.client.controllers;

import bham.bioshock.client.AppPreferences;
import bham.bioshock.client.Client;
import bham.bioshock.common.models.Model;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.server.Server;

import java.util.prefs.Preferences;


/** Root controller used by all other controllers */
public abstract class Controller {

    protected AppPreferences preferences;
    protected Client client;
    protected Model model;
    protected ClientService server;

    public  Controller() {
        preferences = new AppPreferences();
    }
    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }

    public AppPreferences getPreferences() {
        return preferences;
    }
}