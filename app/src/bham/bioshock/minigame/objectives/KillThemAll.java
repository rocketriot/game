package bham.bioshock.minigame.objectives;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.worlds.World;


public class KillThemAll extends Objective {
  private static final long serialVersionUID = 5035692465754355325L;

  private HashMap<UUID, Integer> kills = new HashMap<>();

  @Override
  public UUID getWinner() {
    UUID id = Collections.max(kills.entrySet(), Comparator.comparingInt(HashMap.Entry::getValue))
        .getKey();
    return id;
  }

  @Override
  public void init(World world, Router router, MinigameStore store) {
    super.init(world, router, store);
    store.getPlayers().forEach(player -> {
      kills.put(player.getId(), 0);
    });
  }

  @Override
  public void gotShot(Astronaut player, Astronaut killer) {
    super.gotShot(player, killer);
    if (isDead(player.getId())) {
      if(!player.getId().equals(killer.getId())) {
        addKill(killer);        
      }
      killAndRespawnPlayer(player, getRandomRespawn());
      router.call(Route.MINIGAME_MOVE);
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
    String instructions = "You have 3 minutes to kills as many astronauts as possible! \n"
        + "To kill an astronaut shot him until he loses health";
    return instructions;
  }

  private void addKill(Astronaut p) {
    kills.computeIfPresent(p.getId(), (k, v) -> (v + 1));
  }

  @Override
  protected void updatePlayerHealth(UUID playerId, Integer value) {
    if (isDead(playerId)) {
      killAndRespawnPlayer(localStore.getPlayer(playerId), getRandomRespawn());
      router.call(Route.MINIGAME_MOVE);
    }
  }

}
