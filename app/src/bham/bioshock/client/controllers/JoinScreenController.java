package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.ClientHandler;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.JoinScreen;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.JoinScreenStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.client.CommunicationClient;
import com.google.inject.Inject;

import java.io.Serializable;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JoinScreenController extends Controller {

    private static final Logger logger = LogManager.getLogger(JoinScreenController.class);

    private ClientService clientService;
    private CommunicationClient commClient;
    private ClientHandler clientHandler;
    private BoardGame game;

    @Inject
    public JoinScreenController(Store store, Router router, BoardGame game,
                                CommunicationClient commClient, ClientHandler clientHandler) {
        super(store, router, game);
        this.clientHandler = clientHandler;
        this.commClient = commClient;
        this.game = game;
    }

    public void show(String username) {
        // Create a new player
        Player player = new Player(username);

        // Save player to the store
        store.setMainPlayer(player);

        store.setJoinScreenStore(new JoinScreenStore());
        // Create connection to the server
        try {
            connectToServer(player);
            setScreen(new JoinScreen(router, store, player));

        } catch (ConnectException e) {
            // No server started
            logger.error(e.getMessage());
            router.call(Route.ALERT, e.getMessage());
        }
    }

    public void disconnectPlayer() {
        commClient.getConnection().send(new Action(Command.REMOVE_PLAYER));
    }

    public void removePlayer(UUID id) {
        store.removePlayer(id);
    }

    /**
     * Handle when the server tells us a new player was added to the game
     */
    public void addPlayer(ArrayList<Player> players) {
        for (Player player : players) {
            logger.debug("Player: " + player.getUsername() + " connected");
            store.addPlayer(player);
            ((JoinScreen)game.getScreen()).addPlayer(player);
        }
    }

    /**
     * Create a connection with the server and wait in lobby when a username is entered
     */
    public void connectToServer(Player player) throws ConnectException {
        // Create server connection
        clientService = commClient.connect(player.getUsername());
        clientService.registerHandler(clientHandler);

        // Add the player to the server
        clientService.send(new Action(Command.ADD_PLAYER, player));
    }

    /**
     * Handle when the server tells the client to start the game
     */
    public void start() {
        commClient.getConnection().send(new Action(Command.START_GAME));
        logger.debug("Ready to start! Waiting for the board");
    }


    public void rocketMove(UUID playerId) throws ConnectException {

        ArrayList<Serializable> arguments = new ArrayList<>();
        arguments.add((Serializable) playerId);
        JoinScreen.RocketAnimation animation = ((JoinScreen)game.getScreen()).getMainPlayerAnimation();
        arguments.add((Serializable) animation.getPosition());
        arguments.add((Serializable) (float) animation.getRotation());

        clientService = commClient.getConnection();
        clientService.send(new Action(Command.JOIN_SCREEN_MOVE, arguments));
    }

    public void updateRocket(ArrayList<Serializable> arguments) {
        UUID id = (UUID) arguments.get(0);
        Position pos = (Position) arguments.get(1);
        float rotation = (float) arguments.get(2);

        store.getJoinScreenStore().updateRocket(pos, rotation, id);
    }

}
