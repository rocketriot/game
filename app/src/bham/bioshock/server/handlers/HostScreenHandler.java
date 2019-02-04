package bham.bioshock.server.handlers;

import java.util.ArrayList;
import java.util.UUID;

import bham.bioshock.common.models.Model;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerService;

public class HostScreenHandler {
	
    /**
     * Adds a player to the server and sends the player to all the clients
     */
    public static void addPlayer(Model model, Action action, ServerService service) {
        ArrayList<String> arguments = action.getArguments();
        UUID id = UUID.fromString(arguments.get(0));
        String username = arguments.get(1);

        // Add a player to the model
        Player player = new Player(id, username);
        model.addPlayer(player);

        // Add isCPU to arguments
        action.getArguments().add(String.valueOf(player.isCpu()));

        // Send add player action to all clients
        service.sendToAll(action);

        // If there are the max number of players start the game
        if (model.getPlayers().size() == model.MAX_PLAYERS) {
            service.sendToAll(new Action(Command.START_GAME));
        }
    }

    /**
     * Creates CPU players and starts the game
     */
    public static void startGame(Model model, Action action, ServerService service) {
        // If there is not 4 players, create CPU players
        while (model.getPlayers().size() != model.MAX_PLAYERS) {
            Player player = new Player(true);
            model.addPlayer(player);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(player.getId().toString());
            arguments.add(player.getUsername());
            arguments.add(String.valueOf(player.isCpu()));

            // Send new CPU player to all clients
            service.sendToAll(new Action(Command.ADD_PLAYER, arguments));
        }

        // Tell the clients to start the game
        service.sendToAll(new Action(Command.START_GAME));
    }
}
