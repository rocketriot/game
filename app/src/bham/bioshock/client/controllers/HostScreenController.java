package bham.bioshock.client.controllers;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.UUID;

import bham.bioshock.client.Client;
import bham.bioshock.client.XMLReader;
import bham.bioshock.client.Client.View;
import bham.bioshock.common.models.Model;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.client.CommunicationClient;

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

    /**
     * Create a connection with the server and wait in lobby when a username is
     * entered
     */
    public void connectToServer(String username) throws ConnectException {
        // Create server connection
        server = CommunicationClient.connect(username, client);

        // Create a new player
        Player player = new Player(username);

        // Setup arguments
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add(player.getId().toString());
        arguments.add(player.getUsername());

        // Add the player to the server
        server.send(new Action(Command.ADD_PLAYER, arguments));
    }

    /**
     * Handle when the server tells us a new player was added to the game
     */
    public void onPlayerJoined(Action action) {
        ArrayList<String> arguments = action.getArguments();
        UUID id = UUID.fromString(arguments.get(0));
        String username = arguments.get(1);
        boolean isCpu = Boolean.getBoolean(arguments.get(2));

        model.addPlayer(new Player(id, username, isCpu));

    }

    /**
     * Tells the server to start the game
     */
    public void startGame() {
        server.send(new Action(Command.START_GAME));
    }

    /**
     * Handle when the server tells the client to start the game
     */
    public void onStartGame(Action action) {
        System.out.println("Ready to start!");
        client.changeScreen(Client.View.GAME_BOARD);
    }

    @Override
    public void changeScreen(View view) {
        client.changeScreen(view);
    }
}
