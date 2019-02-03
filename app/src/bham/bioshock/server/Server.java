package bham.bioshock.server;

import java.util.ArrayList;

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
        default:
            break;
        }

        return null;
    }

    public static void main(String args[]) {
        new Server();
    }
}