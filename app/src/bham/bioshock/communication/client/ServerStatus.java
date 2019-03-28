package bham.bioshock.communication.client;

import java.util.UUID;

public class ServerStatus {

  private String name;
  private String ip;
  private UUID recoveredId;
  private String serverId;

  public ServerStatus(String name, String ip, String serverId) {
    this.name = name;
    this.ip = ip;
    this.serverId = serverId;
  }

  public String getId() {
    return serverId;
  }

  public UUID getPlayerId() {
    return recoveredId;
  }

  public void setPlayerId(UUID playerId) {
    this.recoveredId = playerId;
  }

  public String getName() {
    return name;
  }

  public String getIP() {
    return ip;
  }
}
