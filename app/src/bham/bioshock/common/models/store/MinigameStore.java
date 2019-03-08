package bham.bioshock.common.models.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MinigameStore {

  private World currentWorld;
  private UUID mainPlayerId;
  private HashMap<UUID, Astronaut> players;
  private ArrayList<Entity> entities = new ArrayList<>();
  private ArrayList<Entity> staticEntities = new ArrayList<>();
  
  private Skin skin;
  private Objective objective;

  public MinigameStore() {
    players = new HashMap<>();
  }

  public void updatePlayer(UUID playerId, SpeedVector speed, Position pos, PlayerTexture dir, Boolean haveGun) {
    Astronaut p = getPlayer(playerId);
    p.setSpeedVector(speed);
    p.setPosition(pos);
    p.setDirection(dir);
    p.setGun(haveGun);
  }

  // Create world from the seeder
  public void seed(Store store, World world, Objective o) {
    this.currentWorld = world;
    mainPlayerId = store.getMainPlayer().getId();
    Position[] playerPos = world.getPlayerPositions();

    int i = 0;
    for(Player player : store.getPlayers()) {
      Astronaut p = new Astronaut(world, playerPos[i]);
      players.put(player.getId(), p);
      i++;
    }
    
    staticEntities.addAll(world.getPlatforms());
    staticEntities.addAll(world.getRockets());
    entities.addAll(world.getGuns());
    entities.addAll(getPlayers());
    this.objective = o;
    o.setPlayers(getPlayers());
  }
  

  public World getWorld() {
    return currentWorld;
  }

  public Astronaut getPlayer(UUID playerId) {
    return players.get(playerId);
  }

  public Astronaut getMainPlayer() {
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

  public Objective getObjective(){return this.objective;}

  public void setSkin(Skin skin) { this.skin = skin; }

  public Skin getSkin() {
    return skin;
  }

  public void addEntity(Entity e) {
    entities.add(e);
  }

}
