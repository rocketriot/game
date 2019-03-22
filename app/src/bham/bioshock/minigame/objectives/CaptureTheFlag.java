package bham.bioshock.minigame.objectives;

import java.util.Random;
import java.util.UUID;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.objectives.FlagOwnerUpdateMessage;
import bham.bioshock.communication.messages.objectives.KillAndRespawnMessage;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Flag;
import bham.bioshock.minigame.models.Entity.State;
import bham.bioshock.minigame.worlds.World;

public class CaptureTheFlag extends Objective {

  private static final long serialVersionUID = 1940697858386232981L;

  private transient Flag flag;
  private transient UUID flagOwner;
  private Position flagPosition;

  public CaptureTheFlag(World world) {
    Random r = new Random();
    float angle = (r.nextInt(1000) % 360);
    float distance = (float) (world.getPlanetRadius() + r.nextInt(10));
    flagPosition = world.convert(new PlanetPosition(angle, distance));
  }

  @Override
  public UUID getWinner() {
    return getFlagOwner();
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
    if(a.is(State.REMOVING)) return;
    if(!store.isHost()) return;
    
    router.call(Route.SEND_OBJECTIVE_UPDATE, new FlagOwnerUpdateMessage(a.getId()));
  }

  /**
   * Handle flag owner update
   * @param m
   */
  @Override
  public void handle(FlagOwnerUpdateMessage m) {
    this.flagOwner = m.flagOwner;
    Astronaut owner = localStore.getPlayer(flagOwner);
    flag.setOwner(owner);
  }
  
  /**
   * Drop the flag
   * 
   * @param m
   */
  public void handle(KillAndRespawnMessage m) {
    if(!m.playerId.equals(flagOwner)) {
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
    String instructions = "You have 3 minutes to capture the flag! \n "
        + "If an astronaut has it, shot him to steal the flag!";

    return instructions;
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
   * @return Position
   */
  public Position getFlagPosition() {
    return flag.getPos();
  }
}
