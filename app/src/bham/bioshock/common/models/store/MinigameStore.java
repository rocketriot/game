package bham.bioshock.common.models.store;

import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.Upgrade;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.EntityType;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.astronaut.AstronautMove;
import bham.bioshock.minigame.models.astronaut.Equipment;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.CollisionHandler;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

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
  private UUID planetID;

  public MinigameStore() {
    players = new HashMap<>();
  }

  public void updatePlayerStep(
      long time, UUID playerId, SpeedVector speed, Position pos, Equipment eq) {
    Long previous = lastMessage.get(playerId);
    if (previous == null || previous < time) {
      lastMessage.put(playerId, time);
      Astronaut p = getPlayer(playerId);
      p.updateFromServer(speed, pos, eq);
    }
  }

  public void updatePlayerMove(UUID playerId, AstronautMove move) {
    Astronaut p = getPlayer(playerId);
    p.updateMove(move);
  }

  public HashMap<UUID, Long> getLastMessages() {
    return lastMessage;
  }

  // Create world from the seeder
  public void seed(Store store, World world, Objective o, UUID planetId) {
    this.planetID = planetId;
    this.currentWorld = world;

    mainPlayerId = store.getMainPlayerId();
    Position[] playerPos = world.getPlayerPositions();

    int i = 0;
    for (Player player : store.getPlayers()) {
      Astronaut p = new Astronaut(world, playerPos[i], player.getId(), i);
      p.setName(player.getUsername());

      if (player.hasUpgrade(Upgrade.Type.MINIGAME_SHIELD)) {
        p.getEquipment().haveShield = true;
      }

      addUpgrades(store, player, p, planetId);

      players.put(player.getId(), p);
      i++;
    }

    staticEntities.addAll(world.getPlatforms());
    staticEntities.addAll(world.getRockets());
    entities.addAll(world.getGuns());
    entities.addAll(getPlayers());

    objective = o;
  }

  public void addUpgrades(Store store, Player player, Astronaut astronaut, UUID planetId) {
    UUID ownerId = store.getPlanetOwner(planetId);
    if (player.getId().equals(ownerId)) {
      astronaut.getEquipment().haveShield = true;
    }
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
    for (Entity e : entities) {
      if (e.type == EntityType.GUN) {
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

  public Skin getSkin() {
    return skin;
  }

  public void setSkin(Skin skin) {
    this.skin = skin;
  }

  public void addEntity(Entity e) {
    entities.add(e);
  }

  public void started() {
    this.started = true;
  }

  public boolean isStarted() {
    return this.started;
  }

  public void dispose() {
    players.values().forEach(p -> p.remove());
    entities.forEach(e -> e.remove());
  }

  public CollisionHandler getCollisionHandler() {
    return collisionHandler;
  }

  public void setCollisionHandler(CollisionHandler collisionHandler) {
    this.collisionHandler = collisionHandler;
  }

  public UUID getPlanetID() {
    return planetID;
  }

  public Entity getEntity(UUID entityId) {
    for (Entity e : entities) {
      if (e.getId().equals(entityId)) {
        return e;
      }
    }
    return null;
  }
}
