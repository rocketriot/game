package bham.bioshock.minigame.objectives;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Astronaut;

import bham.bioshock.minigame.worlds.World;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;


public class KillThemAll extends Objective {
  private Position respawnPosition;
  private HashMap<Astronaut, Float> health = new HashMap<>();
  private HashMap<Astronaut, Integer> kills = new HashMap<>();
  private float initialHealth = 100.0f;
  private Position[] positions;

  public KillThemAll(World world) {
    super(world);
    this.positions = this.getWorld().getPlayerPositions();
    setRandonRespawnPosition();
  }

  @Override
  public Astronaut getWinner() {
    return Collections.max(kills.entrySet(), Comparator.comparingInt(HashMap.Entry::getValue))
        .getKey();
  }


  @Override
  public void gotShot(Astronaut player, Astronaut killer) {
    if (checkIfdead(player)) {
      addKill(killer);
      player.setPosition(respawnPosition);
      getRouter().call(Route.MINIGAME_MOVE);
      setPlayerHealth(initialHealth, player);
    } else {
      float newHealth = health.get(player) - 5.0f;
      setPlayerHealth(newHealth, player);
    }
  }

  @Override
  public void initialise() {
    getPlayers().forEach(player -> {
      health.put(player, initialHealth);
      kills.put(player, 0);
    });
  }

  @Override
  public void seed(MinigameStore store) {
    return;
  }

  @Override
  public void captured(Astronaut a) { return;}


  private boolean checkIfdead(Astronaut p) {
    if (health.get(p) - 5.0f <= 0)
      return true;
    return false;
  }

  private void setPlayerHealth(float newHealth, Astronaut p) {
    health.computeIfPresent(p, (k, v) -> newHealth);
  }

  private void addKill(Astronaut p) {
    kills.computeIfPresent(p, (k, v) -> (v + 1));
  }

  private void setRandonRespawnPosition() {
    Random r = new Random();
    int i = Math.abs(r.nextInt()%4);
    respawnPosition = positions[i];
  }

}
