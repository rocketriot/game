package bham.bioshock.communication.client;

import java.util.UUID;

public class ServerStatus {

  private String name;
  private String ip;
  private UUID recoveredId;
  
  public ServerStatus(String name, String ip) {
    this.name = name;
    this.ip = ip;
  }
  
  public void setPlayerId(UUID playerId) {
    this.recoveredId = playerId;
  }
  
  public UUID getPlayerId() {
    return recoveredId;
  }
  
  public String getName() {
    return name;
  }

  public String getIP() {
    return ip;
  }
}
