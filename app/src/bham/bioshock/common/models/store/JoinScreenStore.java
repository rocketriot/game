package bham.bioshock.common.models.store;

import bham.bioshock.client.screens.JoinScreen;
import bham.bioshock.common.Position;

import java.util.HashMap;
import java.util.UUID;

public class JoinScreenStore {
    private HashMap<UUID, JoinScreen.RocketAnimation> rocketMap;

    public JoinScreenStore() {
       rocketMap = new HashMap<>();
    }

    public HashMap<UUID, JoinScreen.RocketAnimation> getRocketMap() {
        return rocketMap;
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
        System.out.println("UPDATE ROTATION: " + rotation);
        rocketMap.get(playerID).updatePosition(pos, rotation);
    }

}
