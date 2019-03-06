package bham.bioshock.common.models.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MinigameStore {

  private World currentWorld;
  private UUID mainPlayerId;
  private HashMap<UUID, Astronaut> players;
  private ArrayList<Rocket> rockets;
  private ArrayList<Gun> guns;
  private ArrayList<Entity> otherEntities;
  private Skin skin;
  private Objective objective;

  public MinigameStore() {
    players = new HashMap<>();
    rockets = new ArrayList<>();
    guns = new ArrayList<>();
    otherEntities = new ArrayList<>();
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
    
    this.rockets = world.getRockets();
    this.guns = world.getGuns();

    this.objective = o;
    o.setPlayers(getPlayers());
    o.seed(this);
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

  public Collection<Astronaut> getPlayers() {
    return players.values();
  }
  
  public Collection<Rocket> getRockets() {
    return rockets;
  }

  public Collection<Gun> getGuns() {
    return guns;
  }

  public Collection<Entity> getOthers(){return otherEntities;}

  public void addOther(Entity e){otherEntities.add(e);}
  public void removeGun(Gun g) {
    guns.removeIf(gun -> gun == g);
  }
  public void addGun(Gun g) {
    guns.add(g);
  }
  
  public double getPlanetRadius() {
    return currentWorld.getPlanetRadius();
  }

  public Objective getObjective(){return this.objective;}

  public void setSkin(Skin skin) { this.skin = skin; }

  public Skin getSkin() {
    return skin;
  }



}
