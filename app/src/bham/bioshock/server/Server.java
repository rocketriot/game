package bham.bioshock.server;

import bham.bioshock.common.models.Model;
import bham.bioshock.communication.server.CommunicationServer;
import bham.bioshock.communication.server.ServerHandler;

public class Server extends Thread{
    private Model model;
    private ServerHandler handler;
    
    public Server() {
        this.model = new Model();
        this.handler = new ServerHandler(model);
    }
    
    public void run() {
    	CommunicationServer.start(handler);
    }

    public static void main(String[] args) {
        Server s = new Server();
        s.start();
    }
}