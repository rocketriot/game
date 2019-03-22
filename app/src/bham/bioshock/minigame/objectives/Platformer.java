package bham.bioshock.minigame.objectives;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.objectives.UpdateHealthMessage;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Flag;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.worlds.RandomWorld;
import bham.bioshock.minigame.worlds.World;

import java.util.*;

public class Platformer extends Objective {

  private static final long serialVersionUID = -4384416780407848069L;
  
  private HashMap<UUID, Boolean> frozen = new HashMap<>();
  private HashMap<UUID, Float> frozenFor = new HashMap<>();
  private HashMap<UUID, Float> speedBoost = new HashMap<>();
  private float maxFreeze = 3f;
  private float maxBoost = 3f;
  private transient Optional<UUID> winner = Optional.empty();
  private Goal goal;
  private Platform goalPlatform;

  public Platformer(World world) {
    generateGoalPosition(world);
  }

  @Override
  public UUID getWinner() {
    // if the winner is set, return the winner, if not, return the closest player
    if (winner.isPresent()) {
      return winner.get();
    }
    else {
      Position goalPos = goal.getPos();
      float best = 99999999f;
      Astronaut bestP = null;
      Iterator<Astronaut> it = localStore.getPlayers().iterator();
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
  public void handle(UpdateHealthMessage m) {
    /* when the player is shot, they should freeze for a certain amount of time */
    if (!checkIfFrozen(m.playerId)) {
      setFrozen(m.playerId, true);
    }
  }
  
  @Override
  public void init(World world, Router router, Store store) {
    super.init(world, router, store);
     store.getPlayers().forEach(player -> {
       frozen.put(player.getId(), false);
       frozenFor.put(player.getId(), 0f);
       speedBoost.put(player.getId(), 1f);
     });
     winner = Optional.empty();
  }


  @Override
  public void seed(MinigameStore store) {
    store.addEntity(goal);
  }

  @Override
  public void captured(Astronaut a) {
    if(a.is(Entity.State.REMOVING)) return;
    this.winner = Optional.of(a.getId());

    // make a method that ends the game
    return;
  }

  @Override
  public String instructions() {
    return "You have 3 minutes to find the goal! \n " +
            "The first astronaut to reach it wins!";
  }


  public Platform getGoalPlatform() {
    return goalPlatform;
  }

  private void generateGoalPosition(World world) {
    /* generate a feasable position for the end goal */
    ArrayList<Platform> platforms = world.getPlatforms();
    Platform highestPlatform = platforms.get(0);
    for (int i = 0; i < platforms.size(); i++) {
      Platform platform = platforms.get(i);
      if (platform.getY() > highestPlatform.getY()) {
        highestPlatform = platform;
      }
    }
    /* set the goal to the highest platform */
    goalPlatform = highestPlatform;
    goal = new Goal(world, highestPlatform.getX(), highestPlatform.getY(), true, EntityType.GOAL);
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


  public void boostSpeed(UUID playerId, float boost) {
    if (speedBoost.get(playerId) == null || !(speedBoost.get(playerId) >= boost)) {
      speedBoost.put(playerId, boost);
    }
  }

  public float getSpeedBoost(UUID playerId) {
    return speedBoost.get(playerId);
  }

  public ArrayList<Platform> getPathToGoal() {
    ArrayList<Platform> path = ((RandomWorld) world).getPlatformPath(goalPlatform);
    return path;
  }




}
