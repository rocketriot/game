package bham.bioshock.minigame.objectives;

import java.util.Random;
import java.util.UUID;
import org.nd4j.linalg.primitives.Optional;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Flag;
import bham.bioshock.minigame.models.Entity.State;
import bham.bioshock.minigame.worlds.World;

public class CaptureTheFlag extends Objective {

  private static final long serialVersionUID = 1940697858386232981L;

  private Position flagPosition;
  private transient Flag flag;
  private transient Optional<UUID> flagOwner = Optional.empty();

  public CaptureTheFlag(World world) {
    Random r = new Random();
    float angle = (r.nextInt(1000) % 360);
    float distance = (float) (world.getPlanetRadius() + r.nextInt(500));
    flagPosition = world.convert(new PlanetPosition(angle, distance));
  }

  @Override
  public UUID getWinner() {
    if (flagOwner.isPresent()) {
      return flagOwner.get();
    }
    return null;
  }

  @Override
  public void init(World world, Router router, MinigameStore store) {
    super.init(world, router, store);
    flagOwner = Optional.empty();
  }

  @Override
  public void gotShot(Astronaut player, Astronaut killer) {
    super.gotShot(player, killer);
    if (isDead(player.getId())) {
      flagPosition = (Position) player.getPos();
      killAndRespawnPlayer(player, getRandomRespawn());

      if (flagOwner.isPresent() && flagOwner.get().equals(player.getId())) {
        flag.getPos().x = flagPosition.x;
        flag.getPos().y = flagPosition.y;
        flagOwner = Optional.empty();
      }
    }
  }

  @Override
  public void seed(MinigameStore store) {
    flag = new Flag(world, flagPosition.x, flagPosition.y);
    store.addEntity(flag);
  }

  @Override
  public void captured(Astronaut a) {
    if(a.is(State.REMOVING)) return;
    this.flagOwner = Optional.of(a.getId());
    a.setItem(flag);
  }

  @Override
  public String instructions() {
    String instructions = "You have 3 minutes to capture the flag! \n "
        + "If an astronaut has it, shot him to steal the flag!";

    return instructions;
  }
}
