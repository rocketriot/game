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
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MinigameStore {

  private World currentWorld;
  private UUID mainPlayerId;
  private HashMap<UUID, Player> players;
  private ArrayList<Rocket> rockets;
  private ArrayList<Gun> guns;
  private ArrayList<Entity> dynamic = new ArrayList<>();
  
  private Skin skin;

  public MinigameStore() {
    players = new HashMap<>();
    rockets = new ArrayList<>();
    guns = new ArrayList<>();
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
    
    this.rockets = world.getRockets();
    this.guns = world.getGuns();
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

  public Collection<Gun> getGuns() {
    return guns;
  }
  public void removeGun(Gun g) {
    guns.removeIf(gun -> gun == g);
  }
  public void addGun(Gun g) {
    guns.add(g);
  }
  
  public double getPlanetRadius() {
    return currentWorld.getPlanetRadius();
  }

  public void setSkin(Skin skin) { this.skin = skin; }

  public Skin getSkin() {
    return skin;
  }

  public void addEntity(Bullet b) {
    dynamic.add(b);
  }
  
  public Collection<Entity> getDynamicEntities() {
    return dynamic;
  }



}
