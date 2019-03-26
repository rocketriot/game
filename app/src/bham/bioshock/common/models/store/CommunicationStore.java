package bham.bioshock.common.models.store;

import java.util.ArrayList;
import bham.bioshock.communication.client.ServerStatus;

public class CommunicationStore {

  private ArrayList<ServerStatus> servers = new ArrayList<>();
  private boolean isHost = false;
  private ServerStatus recoveredServer;
  
  public void setHost(boolean value) {
    this.isHost = value;
  }

  public boolean isHost() {
    return isHost;
  }

  public void register(ServerStatus server) {
    servers.removeIf(s -> s.getIP().equals(server.getIP()));
    servers.add(server);
  }
  
  public ArrayList<ServerStatus> getServers() {
    return servers;
  }
  
  public void unregister(String ip) {
    servers.removeIf(s -> s.getIP().equals(ip));
  }
  
  public void setRecoveredServer(ServerStatus server) {
    this.recoveredServer = server;
  }
  public ServerStatus getRecoveredServer() {
    return recoveredServer;
  }

  public void clearServers() {
    servers.clear();
  }

}
