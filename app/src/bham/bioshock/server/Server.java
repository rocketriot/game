package bham.bioshock.server;

import bham.bioshock.common.models.Model;
import bham.bioshock.communication.server.CommunicationServer;
import bham.bioshock.communication.server.ServerHandler;

public class Server {
    private Model model;
    private ServerHandler handler;
    
    public Server() {
        this.model = new Model();
        this.handler = new ServerHandler(model);
        CommunicationServer.start(handler);
    }

    public static void main(String[] args) {
        new Server();
    }
}