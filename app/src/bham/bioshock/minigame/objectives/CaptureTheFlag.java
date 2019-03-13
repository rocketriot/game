package bham.bioshock.minigame.objectives;

import java.util.HashMap;
import java.util.Random;
import bham.bioshock.client.Route;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Flag;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.worlds.World;

public class CaptureTheFlag extends Objective {

  private static final long serialVersionUID = 1940697858386232981L;

  private Position respawnPosition;
  private HashMap<Astronaut, Float> health = new HashMap<>();
  private float initialHealth = 50.0f;
  private Position[] positions;
  private Position flagPosition;
  private Flag flag;
  private Astronaut flagOwner = null;


  public CaptureTheFlag(World world) {
    super(world);
    this.positions = this.getWorld().getPlayerPositions();
    setRandonRespawnPosition();
    Position p = new Position(-2350.0f, 100.0f);
    setFlagPosition(p);


    this.flag = new Flag(world, flagPosition.x, flagPosition.y);
  }

  @Override
  public Astronaut getWinner() {
    return flagOwner;
  }

  // router call minigame move

  @Override
  public void gotShot(Astronaut player, Astronaut killer) {
    if (checkIfdead(player)) {
      setFlagPosition(player.getPos());
      flag.setIsRemoved(false);
      boolean hadGun = player.haveGun();
      player.killAndRespawn(respawnPosition);
      if(hadGun) {
        Gun gun = new Gun(getWorld(), player.getX(), player.getY());
        gun.load();
        this.localSore.addEntity(gun);
      }
      
      getRouter().call(Route.MINIGAME_MOVE);
      setPlayerHealth(initialHealth, player);

    } else {
      float newHealth = health.get(player) - 10.0f;
      setPlayerHealth(newHealth, player);
    }

  }

  @Override
  public void initialise() {
    getPlayers().forEach(player -> {
      health.put(player, initialHealth);
    });

  }

  @Override
  public void seed(MinigameStore store) {
    super.seed(store);
    store.addEntity(flag);
  }

  @Override
  public void captured(Astronaut a) {
    setFlagOwner(a);
  }

  private void setRandonRespawnPosition() {
    Random r = new Random();
    int i = Math.abs(r.nextInt() % 4);
    respawnPosition = positions[i];
  }

  private boolean checkIfdead(Astronaut p) {
    if (health.get(p) - 5.0f <= 0)
      return true;
    return false;
  }

  private void setPlayerHealth(float newHealth, Astronaut p) {
    health.computeIfPresent(p, (k, v) -> newHealth);
  }

  private void setFlagPosition(Position p) {
    flagPosition = p;
  }

  public void setFlagOwner(Astronaut owner) {
    this.flagOwner = owner;
  }
}
