package bham.bioshock.common.models.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;

public class MinigameStore {

  private World currentWorld;
  private UUID mainPlayerId;
  private ConcurrentHashMap<UUID, Player> players;
  private ArrayList<Rocket> rockets;

  public MinigameStore() {
    players = new ConcurrentHashMap<>();
    rockets = new ArrayList<>();
  }

  public void updatePlayer(UUID playerId, SpeedVector speed, Position pos, PlayerTexture dir) {
    Player p = getPlayer(playerId);
    synchronized(p) {
      p.setSpeedVector(speed);
      p.setPosition(pos);
      p.setDirection(dir);      
    }
  }

  // Create world from the seeder
  public void seed(Store store, World world) {
    this.currentWorld = world;
    if(store.getMainPlayer() != null) {
      mainPlayerId = store.getMainPlayer().getId();      
    }
    ArrayList<bham.bioshock.common.models.Player> players = store.getPlayers();
    Position[] playerPos = world.getPlayerPositions();

    for (int i = 0; i < players.size(); i++) {
      Player p = new Player(world, playerPos[i]);
      UUID playerId = players.get(i).getId();
      if(store.isMainPlayer(playerId)) {
        p.setMain();
      }
      this.players.put(playerId, p);
    }
    
    this.rockets = world.getRockets();
  }
  

  public World getWorld() {
    return currentWorld;
  }

  public Player getPlayer(UUID playerId) {
    return players.get(playerId);
  }

  public Player getMainPlayer() {
    return getPlayer(mainPlayerId);
  }

  public Collection<Player> getPlayers() {
    return players.values();
  }

  public Collection<Rocket> getRockets() {
    return rockets;
  }

  public double getPlanetRadius() {
    return currentWorld.getPlanetRadius();
  }

}
