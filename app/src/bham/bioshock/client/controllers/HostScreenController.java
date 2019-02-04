package bham.bioshock.client.controllers;

import java.io.Serializable;
import java.net.ConnectException;

import com.badlogic.gdx.Screen;

import bham.bioshock.client.Client;
import bham.bioshock.client.XMLReader;
import bham.bioshock.client.Client.View;
import bham.bioshock.client.screens.HostScreen;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.CommunicationClient;
import bham.bioshock.server.Server;


public class HostScreenController extends Controller {

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
        Server host = new Server();
        host.start();

    	// Create server connection
        server = CommunicationClient.connect(username, client);

        // Create a new player
        Player player = new Player(username);

        // Add the player to the server
        server.send(new Action(Command.ADD_PLAYER, player));
    }


    /**
     * Handle when the server tells us a new player was added to the game
     */
    public void onPlayerJoined(Action action) {
    	for(Serializable argument : action.getArguments()) {
    		Player player = (Player) argument;
    		model.addPlayer(player);
    		System.out.println("Player: " + player.getUsername() + " connected");
    	}

        //((HostScreen) screen).onPlayerJoined();
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

    public void setScreen(Screen screen) {
        this.screen = (HostScreen) screen;
    }

    @Override
    public void changeScreen(View view) {
        client.changeScreen(view);
    }

}
