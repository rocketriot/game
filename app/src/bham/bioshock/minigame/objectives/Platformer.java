package bham.bioshock.minigame.objectives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Flag;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.World;

public class Platformer extends Objective {

  private static final long serialVersionUID = -4384416780407848069L;
  
  private HashMap<UUID, Boolean> frozen = new HashMap<>();
  private HashMap<UUID, Float> frozenFor = new HashMap<>();
  private HashMap<UUID, Float> speedBoost = new HashMap<>();
  private float maxFreeze = 3f;
  private float maxBoost = 3f;
  private UUID winner;
  private Position goalPosition;
  private Flag goal;

  public Platformer(World world) {
    generateGoalPosition(world);
  }

  @Override
  public UUID getWinner() {
    // if the winner is set, return the winner, if not, return the closest player
    if (winner != null) {
      return winner;
    } else {
      Position goalPos = goal.getPos();
      float best = 99999999f;
      Astronaut bestP = null;
      Iterator<Astronaut> it = getPlayers().iterator();
      while (it.hasNext()) {
        Astronaut player = (Astronaut) it.next();
        Position playerPos = player.getPos();
        float diff = playerPos.sqDistanceFrom(goalPos);
        if (diff < best) {
          best = diff;
          bestP = player;
        }
      }
      return bestP != null ? bestP.getId() : null;
    }
  }

  @Override
  public void gotShot(Astronaut player, Astronaut shooter) {
    super.gotShot(player, shooter);
    /* when the player is shot, they should freeze for a certain amount of time */
    if (!checkIfFrozen(player.getId())) {
      setFrozen(player.getId(), true);
    }
  }
  
  @Override
  public void init(World world, Router router, MinigameStore store) {
    super.init(world, router, store);
     store.getPlayers().forEach(player -> {
       frozen.put(player.getId(), false);
       frozenFor.put(player.getId(), 0f);
       speedBoost.put(player.getId(), 1f);
     });
  }


  @Override
  public void seed(MinigameStore store) {
    store.addEntity(goal);
  }

  @Override
  public void captured(Astronaut a) {
    return;
  }

  @Override
  public String instructions() {
    return "";
  }

  public Position getGoalPosition() {
    return goalPosition;
  }

  private void generateGoalPosition(World world) {
    /* generate a feasable position for the end goal */
    ArrayList<Platform> platforms = world.getPlatforms();
    Position highestPlatform = platforms.get(0).getPos();
    for (int i = 0; i < platforms.size(); i++) {
      Platform platform = world.getPlatforms().get(i);
      if (platform.getY() > highestPlatform.y) {
        highestPlatform = platform.getPos();
      }
    }
    /* set the goal to the highest platform */
    goalPosition = highestPlatform;
    goal = new Flag(world, highestPlatform.x, highestPlatform.y);
  }


  public boolean checkIfFrozen(UUID playerId) {
    Boolean isFrozen = frozen.get(playerId);
    return isFrozen != null && frozen.get(playerId);
  }

  public void setFrozen(UUID playerId, boolean status) {
    frozen.put(playerId, status);
    frozenFor.put(playerId, 0f);
  }

  public void countDown(float delta) {
    for (Map.Entry<UUID, Float> astronautFloatEntry : frozenFor.entrySet()) {
      float newValue = astronautFloatEntry.getValue().floatValue() + delta;
      if (newValue >= maxFreeze) {
        setFrozen(astronautFloatEntry.getKey(), false);
      }
    }
  }

  public void reachedGoal(Astronaut winner) {
    this.winner = winner.getId();
  }

  public void boostSpeed(UUID playerId, float boost) {
    if (speedBoost.get(playerId) == null || !(speedBoost.get(playerId) >= boost)) {
      speedBoost.put(playerId, boost);
    }
  }

  public float getSpeedBoost(UUID playerId) {
    return speedBoost.get(playerId);
  }

}
