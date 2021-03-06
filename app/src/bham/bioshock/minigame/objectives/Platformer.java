package bham.bioshock.minigame.objectives;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.objectives.EndPlatformerMessage;
import bham.bioshock.communication.messages.objectives.UpdateFrozenMessage;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Goal;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.World;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class Platformer extends Objective {

  private static final long serialVersionUID = -4384416780407848069L;
  public float MAX_FROZEN_TIME = 5;
  /** Maps players to whether or not they are frozen. */
  private HashMap<UUID, Long> frozenFor = new HashMap<>();

  private transient Optional<UUID> winner = Optional.empty();
  private Goal goal;
  private Platform goalPlatform;

  public Platformer(World world) {
    generateGoalPosition(world);
  }

  /**
   * called when the minigame ends. If the goal has been captured, there will be a winner,
   * otherwise, return the player that is closest to the goal.
   *
   * @return the UUID of the winning player.
   */
  @Override
  public UUID getWinner() {
    // if the winner is set, return the winner, if not, return the closest player
    if (winner.isPresent()) {
      return winner.get();
    } else {
      Position goalPos = goal.getPos();
      float best = 99999999f;
      Astronaut bestP = null;
      Iterator<Astronaut> it = localStore.getPlayers().iterator();
      while (it.hasNext()) {
        Astronaut player = it.next();
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
  public MinigameType getMinigameType() {
    return MinigameType.PLATFORMER;
  }

  /**
   * Send a message to the server to freeze a player, if they get shot.
   *
   * @param player: the player who got shot
   * @param killer: the player who shot
   */
  @Override
  public void gotShot(Astronaut player, UUID killer) {
    if (!store.isHost()) {
      return;
    }
    router.call(Route.SEND_OBJECTIVE_UPDATE, new UpdateFrozenMessage(player.getId()));
  }

  /**
   * Called when the objective recives a message from the server. This will be a message to tell
   * that a player has been shot and should be frozen.
   *
   * @param m the message
   */
  @Override
  public void handle(UpdateFrozenMessage m) {
    /* when the player is shot, they should freeze for a certain amount of time */
    setFrozen(m.playerID, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
  }

  /**
   * Set up the objective. Add all the players to the frozen map, initially mapped to false (not
   * frozen). Add all the players to the frozenFor map, initially mapped to 0 (frozen for no time).
   *
   * @param world
   * @param router
   * @param store
   */
  @Override
  public void init(World world, Router router, Store store) {
    super.init(world, router, store);
    winner = Optional.empty();
  }

  @Override
  public void seed(MinigameStore store) {
    store.addEntity(goal);
  }

  /**
   * Called when the goal is reached. Sets the winner to the player that reached it. Sends a message
   * to the server to end the minigame.
   *
   * @param a: the player who got the flag
   */
  @Override
  public void captured(Astronaut a) {
    if (a.is(Entity.State.REMOVING)) return;
    this.winner = Optional.of(a.getId());

    // Only one message should be sent
    if (!store.isHost()) {
      return;
    }

    router.call(Route.SEND_OBJECTIVE_UPDATE, new EndPlatformerMessage(a.getId()));
    return;
  }

  @Override
  public String instructions() {
    return "You have 1 minute to find the goal,\n" + "the first player that reaches it wins!";
  }

  @Override
  public String name() {
    return "Find the Goal!";
  }

  public Platform getGoalPlatform() {
    return goalPlatform;
  }

  /**
   * Generate a feasable position for the goal to be rendered. The world is made of layers of
   * platforms. The most suitable position would be on top of the highest platform.
   *
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
    goal = new Goal(world, highestPlatform.getX(), highestPlatform.getY());
  }

  /**
   * Check if a player is frozen. If so they should not move.
   *
   * @param playerId The UUID of the player that will be checked
   * @return if the player is frozen or not
   */
  public boolean checkIfFrozen(UUID playerId) {
    Long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    Long frozen = getFrozenFor(playerId);
    if (frozen == 0) {
      return false;
    }

    if ((now - frozen) < MAX_FROZEN_TIME) {
      return true;
    }

    return false;
  }

  /**
   * Save the time the player was frozen
   *
   * @param playerId
   * @param status
   */
  public void setFrozen(UUID playerId, long time) {
    frozenFor.put(playerId, time);
  }

  public long getFrozenFor(UUID playerId) {
    if (frozenFor.containsKey(playerId)) {
      return frozenFor.get(playerId);
    } else {
      return 0;
    }
  }

  /**
   * Get the sequence of platforms by following which a player can travel from the ground to the
   * goal This is used by the CPU players
   *
   * @return a path from the ground to the goal
   */
  public ArrayList<Platform> getPathToGoal() {
    ArrayList<Platform> path = world.getPlatformPath(goalPlatform);
    return path;
  }
}
