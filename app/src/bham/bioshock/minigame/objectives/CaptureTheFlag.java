package bham.bioshock.minigame.objectives;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.objectives.FlagOwnerUpdateMessage;
import bham.bioshock.communication.messages.objectives.KillAndRespawnMessage;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Entity.State;
import bham.bioshock.minigame.models.Flag;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.World;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/** The Capture the Flag objective. */
public class CaptureTheFlag extends Objective {

  private static final long serialVersionUID = 1940697858386232981L;

  private transient Flag flag;
  private transient UUID flagOwner;
  private Position flagPosition;

  /**
   * Instantiates a new Capture The Flag objective.
   *
   * @param world the world
   */
  public CaptureTheFlag(World world) {
    Random r = new Random();
    ArrayList<Platform> allPlatforms = world.getPlatforms();
    ArrayList<Platform> platforms = new ArrayList<>();

    for (int i = 0; i < (int) Math.ceil(allPlatforms.size() / 4); i++) {
      platforms.add(allPlatforms.get(i));
    }

    PlanetPosition pPos;
    if (platforms.size() == 0) {
      float angle = (float) (0 + Math.random() * 359);
      int distance =
          (int) (Math.random() * (world.getPlanetRadius() + 200) + (world.getPlanetRadius() + 50));
      pPos = new PlanetPosition(angle, distance);
    } else {
      pPos = platforms.get(r.nextInt(platforms.size())).getPlanetPos();
      pPos.fromCenter += 25;
    }
    flagPosition = world.convert(pPos);
  }

  @Override
  public UUID getWinner() {
    return getFlagOwner();
  }

  @Override
  public MinigameType getMinigameType() {
    return MinigameType.CAPTURE_THE_FLAG;
  }

  @Override
  public void init(World world, Router router, Store store) {
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
    if (a.is(State.REMOVING)) {
      return;
    }
    if (!store.isHost()) {
      return;
    }

    router.call(Route.SEND_OBJECTIVE_UPDATE, new FlagOwnerUpdateMessage(a.getId()));
  }

  /** Handle flag owner update */
  @Override
  public void handle(FlagOwnerUpdateMessage m) {
    this.flagOwner = m.flagOwner;
    Astronaut owner = localStore.getPlayer(flagOwner);
    flag.setOwner(owner);
  }

  /** Drop the flag */
  public void handle(KillAndRespawnMessage m) {
    if (!m.playerId.equals(flagOwner)) {
      super.handle(m);
      return;
    }
    // Get position before respawn
    Astronaut owner = localStore.getPlayer(flagOwner);
    flagPosition = owner.getPos();

    // Kill and respawn player
    super.handle(m);

    // Reset owner and update position
    flag.setOwner(null);
    flag.setPosition(flagPosition.x, flagPosition.y);
  }

  @Override
  public String instructions() {
    String instructions =
        "You have 1 minute to capture the flag,\n" + "steal the flag by shooting other players!";

    return instructions;
  }

  @Override
  public String name() {
    return "Capture the Flag!";
  }

  /**
   * Get current flag owner
   *
   * @return UUID of the owner
   */
  public UUID getFlagOwner() {
    return flagOwner;
  }

  /**
   * Get current flag position
   *
   * @return Position of the flag position
   */
  public Position getFlagPosition() {
    return flag.getPos();
  }
}
