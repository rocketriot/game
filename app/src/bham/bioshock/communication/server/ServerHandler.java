package bham.bioshock.communication.server;

import java.util.ArrayList;

public class ServerHandler {
	
	private ArrayList<ServerService> connections;
	
	public ServerHandler() {
		connections = new ArrayList<>();
	}
	
	public void register(ServerService service) {
		connections.add(service);
	}
}
