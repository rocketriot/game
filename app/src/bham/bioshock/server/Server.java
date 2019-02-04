package bham.bioshock.server;

import bham.bioshock.common.models.Model;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.server.CommunicationServer;
import bham.bioshock.communication.server.ServerHandler;
import bham.bioshock.communication.server.ServerService;
import bham.bioshock.server.handlers.*;

public class Server extends Thread {
    private Model model;

    public Server() {
        this.model = new Model();
    }

    public void run() {
        CommunicationServer.start(new ServerHandler(), this);
    }

    public void handleRequest(Action action, ServerService service) {
        switch (action.getCommand()) {
        case ADD_PLAYER:
            JoinScreenHandler.addPlayer(model, action, service);
            break;
        case START_GAME:
            JoinScreenHandler.startGame(model, action, service);
            break;
            break;
        default:
            System.out.println("Received unhandled command: " + action.getCommand().toString());
            break;
        }
    }

    public static void main(String[] args) {
        (new Server()).start();
    }
}