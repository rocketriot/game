package bham.bioshock.common.models.store;

import bham.bioshock.communication.client.ServerStatus;

import java.util.ArrayList;
import java.util.Comparator;

public class CommunicationStore {

  private ArrayList<ServerStatus> servers = new ArrayList<>();
  private boolean isHost = false;
  private ServerStatus recoveredServer;

  public boolean isHost() {
    return isHost;
  }

  public void setHost(boolean value) {
    this.isHost = value;
  }

  public void register(ServerStatus server) {
    servers.removeIf(s -> s.getId().equals(server.getId()));
    servers.add(server);

    servers.sort(
        new Comparator<ServerStatus>() {

          @Override
          public int compare(ServerStatus arg0, ServerStatus arg1) {
            return arg0.getId().compareTo(arg1.getId());
          }
        });
  }

  public ArrayList<ServerStatus> getServers() {
    return servers;
  }

  public void unregister(String ip) {
    servers.removeIf(s -> s.getIP().equals(ip));
  }

  public ServerStatus getRecoveredServer() {
    return recoveredServer;
  }

  public void setRecoveredServer(ServerStatus server) {
    this.recoveredServer = server;
  }

  public void clearServers() {
    servers.clear();
  }
}
