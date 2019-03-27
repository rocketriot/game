package bham.bioshock.minigame.objectives;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.objectives.KillAndRespawnMessage;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.worlds.World;


public class KillThemAll extends Objective {

  private static final long serialVersionUID = 5035692465754355325L;

  /**
   * Maps players to the number of kills they have
   */
  private HashMap<UUID, Integer> kills = new HashMap<>();

  @Override
  public UUID getWinner() {
    UUID id = Collections.max(kills.entrySet(), Comparator.comparingInt(HashMap.Entry::getValue))
        .getKey();
    return id;
  }

  @Override
  public void init(World world, Router router, Store store) {
    super.init(world, router, store);
    store.getPlayers().forEach(player -> {
      kills.put(player.getId(), 0);
    });
  }

  public void handle(KillAndRespawnMessage m) {
    super.handle(m);
    if (!m.playerId.equals(m.shooterId)) {
      addKill(m.shooterId);
    }
  }


  @Override
  public void seed(MinigameStore store) {
    return;
  }

  @Override
  public void captured(Astronaut a) {
    return;
  }

  @Override
  public String instructions() {
    String instructions = "You have 1 minute to kill as many players as possible,\n"
        + "kill an astronaut by shooting them!";
    return instructions;
  }

  @Override
  public String name() {
    return "Kill Them All!";
  }

  /**
   * Update killer stats
   */
  private void addKill(UUID astronautId) {
    kills.computeIfPresent(astronautId, (k, v) -> (v + 1));
  }
}
