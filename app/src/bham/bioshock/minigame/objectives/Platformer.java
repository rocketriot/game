package bham.bioshock.minigame.objectives;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.minigame.EndMinigameMessage;
import bham.bioshock.communication.messages.objectives.UpdateHealthMessage;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.worlds.World;

import java.util.*;

public class Platformer extends Objective {

  private static final long serialVersionUID = -4384416780407848069L;

  /**
   * Maps players to whether or not they are frozen.
   */
  private HashMap<UUID, Boolean> frozen = new HashMap<>();
  private HashMap<UUID, Float> frozenFor = new HashMap<>();
  private float maxFreeze = 3f;
  private transient Optional<UUID> winner = Optional.empty();
  private Goal goal;
  private Platform goalPlatform;

  public Platformer(World world) {
    generateGoalPosition(world);
  }

  /**
   * called when the minigame ends. If the goal has been captured, there will be a winner,
   * otherwise, return the player that is closest to the goal.
   * @return the UUID of the winning player.
   */
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

  /**
   * Called when the objective recives a message from the server.
   * This will be a message to tell that a player has been shot.
   * @param m the message
   */
  @Override
  public void handle(UpdateHealthMessage m) {
    /* when the player is shot, they should freeze for a certain amount of time */
    if (!checkIfFrozen(m.playerId)) {
      setFrozen(m.playerId, true);
    }
  }

  /**
   * Set up the objective.
   * Add all the players to the frozen map, initially mapped to false (not frozen).
   * Add all the players to the frozenFor map, initially mapped to 0 (frozen for no time).
   * @param world
   * @param router
   * @param store
   */
  @Override
  public void init(World world, Router router, Store store) {
    super.init(world, router, store);
     store.getPlayers().forEach(player -> {
       frozen.put(player.getId(), false);
       frozenFor.put(player.getId(), 0f);
     });
     winner = Optional.empty();
  }


  @Override
  public void seed(MinigameStore store) {
    store.addEntity(goal);
  }

  /**
   * Called when the goal is reached. Sets the winner to the player that reached it.
   * Sends a message to the server to end the minigame.
   *
   * @param a: the player who got the flag
   */
  @Override
  public void captured(Astronaut a) {
    if(a.is(Entity.State.REMOVING)) return;
    this.winner = Optional.of(a.getId());

    //Only one message should be sent
    if (!store.isHost()){
      return;
    }

    Player initiator = store.getMovingPlayer();
    router.call(Route.MINIGAME_END, new EndMinigameMessage(initiator.getId(), winner.get(), localStore.getPlanetID(), 75));

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

  /**
   * Generate a feasable position for the goal to be rendered.
   * The world is made of layers of platforms. The most suitable position would be on top of
   * the highest platform.
   * @param world the minigame worl. This contains all platforms.
   */
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

  /**
   * Check if a player is frozen. If so they should not move.
   * @param playerId The UUID of the player that will be checked
   * @return if the player is frozen or not
   */
  public boolean checkIfFrozen(UUID playerId) {
    Boolean isFrozen = frozen.get(playerId);
    return isFrozen != null && frozen.get(playerId);
  }

  /**
   * Freeze or unfreeze a player
   * @param playerId
   * @param status
   */
  public void setFrozen(UUID playerId, boolean status) {
    frozen.put(playerId, status);
    frozenFor.put(playerId, 0f);
  }

  /**
   * Players only remain frozen for a certain amount of time. Everytime the game updates, increment how long
   * a player has been frozen for. If it's over the maximum freeze time, unfreeze the player.
   * @param delta
   */
  public void countDown(float delta) {
    for (Map.Entry<UUID, Float> astronautFloatEntry : frozenFor.entrySet()) {
      float newValue = astronautFloatEntry.getValue().floatValue() + delta;
      if (newValue >= maxFreeze) {
        setFrozen(astronautFloatEntry.getKey(), false);
      }
    }
  }

  /**
   * Get the sequence of platforms by following which a player can travel from the ground to the goal
   * This is used by the CPU players
   * @return a path from the ground to the goal
   */
  public ArrayList<Platform> getPathToGoal() {
    ArrayList<Platform> path = world.getPlatformPath(goalPlatform);
    return path;
  }




}
