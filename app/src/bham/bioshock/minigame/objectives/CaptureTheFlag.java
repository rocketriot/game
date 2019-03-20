package bham.bioshock.minigame.objectives;

import java.util.Random;
import java.util.UUID;
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

  private transient Flag flag;
  private Position flagPosition;
  private UUID flagOwner = null;

  public CaptureTheFlag(World world) {
    Random r = new Random();
    float angle = (r.nextInt(1000) % 360);
    float distance = (float) (world.getPlanetRadius() + r.nextInt(500));
    flagPosition = world.convert(new PlanetPosition(angle, distance));
  }

  @Override
  public UUID getWinner() {
    if (flagOwner != null) {
      return flagOwner;
    }
    return null;
  }

  @Override
  public void init(World world, Router router, MinigameStore store) {
    super.init(world, router, store);
    flagOwner = null;
  }

  @Override
  public void seed(MinigameStore store) {
    flag = new Flag(world, flagPosition.x, flagPosition.y);
    flag.load();
    store.addEntity(flag);
  }

  @Override
  public void captured(Astronaut a) {
    if(a.is(State.REMOVING)) return;
    this.flagOwner = a.getId();
  }

  @Override
  public String instructions() {
    String instructions = "You have 3 minutes to capture the flag! \n "
        + "If an astronaut has it, shot him to steal the flag!";

    return instructions;
  }

  public void updateFlagOwner(UUID ownerId) {
    this.flagOwner = ownerId;
    Astronaut owner = localStore.getPlayer(flagOwner);
    flag.setOwner(owner);
    flagPosition = flag.getPos();
  }
  
  @Override
  protected void updatePlayerHealth(UUID playerId, Integer value) {
    if (isDead(playerId)) {
      flag.removeOwner();
      Astronaut player = localStore.getPlayer(playerId);
      flagPosition = (Position) player.getPos();
      killAndRespawnPlayer(player, getRandomRespawn());

      if (flagOwner != null && flagOwner.equals(player.getId())) {
        flag.getPos().x = flagPosition.x;
        flag.getPos().y = flagPosition.y;
        flagOwner = null;
      }
    }
  }

  public UUID getFlagOwner() {
    return flagOwner;
  }
}
