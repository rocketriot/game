package bham.bioshock.common.models.store;

import bham.bioshock.client.screens.JoinScreen;
import bham.bioshock.client.screens.JoinScreen.RocketAnimation;
import bham.bioshock.common.Position;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JoinScreenStore {
  private ConcurrentHashMap<UUID, JoinScreen.RocketAnimation> rocketMap;

  public JoinScreenStore() {
    rocketMap = new ConcurrentHashMap<>();
  }

  public void addRocket(UUID id, JoinScreen.RocketAnimation anim) {
    rocketMap.put(id, anim);
  }

  /**
   *
   * @param pos position of a player
   * @param playerID the id of a player that will be moved
   */
  public void updateRocket(Position pos, float rotation, UUID playerID) {
    rocketMap.get(playerID).updatePosition(pos, rotation);
  }

  public void removeRocket(UUID playerId) {
    rocketMap.remove(playerId);
  }

  public int getRocketNum() {
    return rocketMap.size();
  }

  public Collection<RocketAnimation> getRockets() {
    return rocketMap.values();
  }

}
