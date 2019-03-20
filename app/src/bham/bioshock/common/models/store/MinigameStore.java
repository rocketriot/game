package bham.bioshock.common.models.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.models.Astronaut.Move;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.CollisionHandler;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;

public class MinigameStore {

  private World currentWorld;
  private UUID mainPlayerId;
  private HashMap<UUID, Astronaut> players;
  private HashMap<UUID, Long> lastMessage = new HashMap<>();
  private ArrayList<Entity> entities = new ArrayList<>();
  private ArrayList<Entity> staticEntities = new ArrayList<>();

  private Skin skin;
  private CollisionHandler collisionHandler;
  private Objective objective;
  private boolean started;

  public MinigameStore() {
    players = new HashMap<>();
  }

  public void updatePlayerStep(long time, UUID playerId, SpeedVector speed, Position pos,
      Boolean haveGun) {
    Long previous = lastMessage.get(playerId);
    if(previous == null || previous < time) {
      lastMessage.put(playerId, time);
      Astronaut p = getPlayer(playerId);
      p.updateFromServer(speed, pos, haveGun);      
    }
  }
  
  public void updatePlayerMove(UUID playerId, Move move) {
    Astronaut p = getPlayer(playerId);
    p.updateMove(move);
  }

  public HashMap<UUID, Long> getLastMessages() {
    return lastMessage;
  }
  
  // Create world from the seeder
  public void seed(Store store, World world, Objective o) {
    this.currentWorld = world;
    mainPlayerId = store.getMainPlayer().getId();
    Position[] playerPos = world.getPlayerPositions();

    int i = 0;
    for (Player player : store.getPlayers()) {
      Astronaut p = new Astronaut(world, playerPos[i], player.getId(), i);
      p.setName(player.getUsername());
      players.put(player.getId(), p);
      i++;
    }

    staticEntities.addAll(world.getPlatforms());
    staticEntities.addAll(world.getRockets());
    entities.addAll(world.getGuns());
    entities.addAll(getPlayers());

    objective = o;
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

  public Collection<Gun> getGuns() {
    ArrayList<Gun> guns = new ArrayList<>();
    for(Entity e : entities) {
      if(e.type == EntityType.GUN) {
        guns.add((Gun) e);        
      }
    }
    return guns;
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

  public Collection<Astronaut> getPlayers() {
    return players.values();
  }

  public double getPlanetRadius() {
    return currentWorld.getPlanetRadius();
  }

  public Objective getObjective() {
    return this.objective;
  }

  public void setSkin(Skin skin) {
    this.skin = skin;
  }

  public Skin getSkin() {
    return skin;
  }

  public void addEntity(Entity e) {
    entities.add(e);
  }

  public void started(){
    this.started = true;
  }

  public boolean isStarted(){
    return this.started;
  }

  public void setCollisionHandler(CollisionHandler collisionHandler) {
    this.collisionHandler = collisionHandler;
  }
  
  public CollisionHandler getCollisionHandler() {
    return collisionHandler;
  }

}
