package bham.bioshock.minigame.objectives;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import org.nd4j.linalg.primitives.Optional;
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
  private Position flagPosition;
  private Flag flag;
  private Optional<Astronaut> flagOwner = Optional.empty();


  public CaptureTheFlag(World world) {
    setRandonRespawnPosition();
    Position p = new Position(-2350.0f, 100.0f);
    setFlagPosition(p);

    flag = new Flag(world, flagPosition.x, flagPosition.y);
  }

  @Override
  public UUID getWinner() {
    if(flagOwner.isPresent()) {
      return flagOwner.get().getId();
    }
    return null;
  }


  @Override
  public void gotShot(Astronaut player, Astronaut killer) {
    if (checkIfdead(player)) {
      setFlagPosition(player.getPos());
      flag.setIsRemoved(false);
      boolean hadGun = player.haveGun();
      player.killAndRespawn(respawnPosition);
      if(hadGun) {
        Gun gun = new Gun(world, player.getX(), player.getY());
        gun.load();
        this.localStore.addEntity(gun);
      }
      
      router.call(Route.MINIGAME_MOVE);
      setPlayerHealth(initialHealth, player);

    } else {
      float newHealth = health.get(player) - 10.0f;
      setPlayerHealth(newHealth, player);
    }

  }

//  @Override
//  public void initialise() {
//    getPlayers().forEach(player -> {
//      health.put(player, initialHealth);
//    });
//  }

  @Override
  public void seed(MinigameStore store) {
    store.addEntity(flag);
  }

  @Override
  public void captured(Astronaut a) {
    setFlagOwner(a);
  }

  @Override
  public String instructions() {
    String instructions = "You have 3 minutes to capture the flag! \n " +
            "If an astronaut has it, shot him to steal the flag!";

    return instructions;
  }

  private void setRandonRespawnPosition() {
    Random r = new Random();
    int i = Math.abs(r.nextInt() % 4);
    respawnPosition = world.getPlayerPositions()[i];
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
    this.flagOwner = Optional.of(owner);
  }
}
