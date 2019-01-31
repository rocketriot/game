package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.XMLReader;
import bham.bioshock.common.models.Model;
import bham.bioshock.communication.client.ClientService;

public class HostScreenController implements Controller {
    private Client client;
    private ClientService server;
    private Model model;
    private XMLReader game_reader;
    private XMLReader pref_reader;

    public HostScreenController(Client client) {
        this.client = client;
        this.server = client.getServer();
        this.model = client.getModel();
        game_reader = new XMLReader("app/assets/XML/game_desc.xml");
        pref_reader = new XMLReader("app/assets/Preferences/Preferences.XML");
    }

    public int getMaxPlayers() {
        return game_reader.getInt("max_players");
    }

    public int getPreferredPlayers() {
        return pref_reader.getInt("players");
    }
    @Override
    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }

    public void configureGame(String host_name, int no_players) {
        //start a new game connection
    }
}
