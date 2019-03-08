package bham.bioshock.common.models.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.StaticEntity;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MinigameStore {

  private World currentWorld;
  private UUID mainPlayerId;
  private HashMap<UUID, Player> players;
  private ArrayList<Entity> entities = new ArrayList<>();
  private ArrayList<Entity> staticEntities = new ArrayList<>();
  
  private Skin skin;

  public MinigameStore() {
    players = new HashMap<>();
  }

  public void updatePlayer(UUID playerId, SpeedVector speed, Position pos, PlayerTexture dir, Boolean haveGun) {
    Player p = getPlayer(playerId);
    p.setSpeedVector(speed);
    p.setPosition(pos);
    p.setDirection(dir);
    p.setGun(haveGun);
  }

  // Create world from the seeder
  public void seed(Store store, World world) {
    this.currentWorld = world;
    if(store.getMainPlayer() != null) {
      mainPlayerId = store.getMainPlayer().getId();      
    }
    Position[] playerPos = world.getPlayerPositions();

    int i = 0;
    for(bham.bioshock.common.models.Player player : store.getPlayers()) {
      Player p = new Player(world, playerPos[i]);
      players.put(player.getId(), p);
    }
    
    staticEntities.addAll(world.getPlatforms());
    staticEntities.addAll(world.getRockets());
    entities.addAll(world.getGuns());
    entities.addAll(getPlayers());
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
  
  public Collection<Entity> getEntities() {
    return entities;
  }
  
  public Collection<Entity> getStaticEntities() {
    return staticEntities;
  }
  
  public int countEntities() {
    return entities.size() + staticEntities.size();
  }
  
  public Collection<Player> getPlayers() {
    return players.values();
  }
  
  public void removeGun(Gun g) {
    entities.removeIf(gun -> gun == g);
  }
  public void addGun(Gun g) {
    entities.add(g);
  }
  
  public double getPlanetRadius() {
    return currentWorld.getPlanetRadius();
  }

  public void setSkin(Skin skin) { this.skin = skin; }

  public Skin getSkin() {
    return skin;
  }

  public void addEntity(Entity e) {
    entities.add(e);
  }

}
