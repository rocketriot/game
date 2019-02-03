package bham.bioshock.server;

import bham.bioshock.common.models.Model;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.server.CommunicationServer;
import bham.bioshock.communication.server.ServerHandler;
import bham.bioshock.communication.server.ServerService;
import bham.bioshock.server.handlers.HostScreenHandler;

public class Server {
    private Model model;

    public Server() {
        this.model = new Model();

        CommunicationServer.start(new ServerHandler(), this);
    }

    public void handleRequest(Action action, ServerService service) {
        switch (action.getCommand()) {
        case ADD_PLAYER:
            HostScreenHandler.addPlayer(model, action, service);
            break;
        case START_GAME:
            HostScreenHandler.startGame(model, action, service);
            break;
        default:
            System.out.println("Received unhandled command: " + action.getCommand().toString());
            break;
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}