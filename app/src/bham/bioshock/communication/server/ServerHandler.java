package bham.bioshock.communication.server;

import java.util.ArrayList;

import bham.bioshock.communication.Action;

public class ServerHandler {
	
	private ArrayList<ServerService> connections;
	
	public ServerHandler() {
		connections = new ArrayList<>();
	}
	
	public void register(ServerService service) {
		connections.add(service);
	}
	
	public ArrayList<ServerService> getConnections() {
		return connections;
	}
	
	public void sendToAll(Action action) {
		for(ServerService s : connections) {
			s.send(action);
		}
	}
	
	public void sendTo(int clientId, Action action) {
		// TODO
	}
}
