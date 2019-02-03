package bham.bioshock.server;

import java.util.ArrayList;
import java.util.UUID;

import bham.bioshock.common.models.Model;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.CommunicationServer;
import bham.bioshock.communication.server.ServerHandler;
import bham.bioshock.communication.server.ServerService;

public class Server {
    private Model model;

    public Server() {
        this.model = new Model();

        CommunicationServer.start(new ServerHandler(), this);
    }

    public Action handleRequest(Action action, ServerService service) {
        ArrayList<String> arguments = action.getArguments();

        switch (action.getCommand()) {
        case ADD_PLAYER:
            UUID id = UUID.fromString(arguments.get(0));
            String username = arguments.get(1);

            model.addPlayer(new Player(id, username));

            service.sendToAll(action);
        default:
            break;
        }

        return null;
    }

    public static void main(String args[]) {
        new Server();
    }
}