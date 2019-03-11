package bham.bioshock.server.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;
import bham.bioshock.minigame.Clock;
import bham.bioshock.minigame.ai.KillEveryoneAI;
import bham.bioshock.minigame.objectives.CaptureTheFlag;
import bham.bioshock.minigame.objectives.KillThemAll;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.objectives.Platformer;
import bham.bioshock.minigame.worlds.FirstWorld;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.server.ai.MinigameAILoop;

public class MinigameHandler {

  Store store;
  ServerHandler handler;
  MinigameAILoop aiLoop;

  public MinigameHandler(Store store, ServerHandler handler) {
    this.store = store;
    this.handler = handler;
  }

  /*
   * Create and seed the world, and send start game command to all clients
   */
  public void startMinigame(Action action) {
    // Create a world for the minigame
    World w = new FirstWorld();
    Objective o;

    Random rand = new Random();

    switch(rand.nextInt(4)) {
      case 1:
        o = new CaptureTheFlag(w);
        break;
      case 2:
        o = new Platformer(w);
        break;
      case 3:
        o = new KillThemAll(w);
        break;
      default:
        o = new CaptureTheFlag(w);
        break;
    }
    //
    aiLoop = new MinigameAILoop();
    aiLoop.start();

    for (UUID id : store.getCpuPlayers()) {
      aiLoop.registerHandler(new KillEveryoneAI(id, store, handler));
    }

    Serializable arg = (Serializable) w;
    Serializable arg2 = (Serializable) o;

    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add(arg);
    arguments.add(arg2);
    handler.sendToAll(new Action(Command.MINIGAME_START, arguments));
  }

  /*
   * Sync player movement and position
   */
  public void playerMove(Action action, UUID playerId) {
    handler.sendToAllExcept(action, playerId);
  }

  /*
   * Sync player movement and position
   */
  public void bulletShot(Action action, UUID playerId) {
    handler.sendToAllExcept(action, playerId);
  }

  private void checkTime(float delta) {
    Clock clock = new Clock();
    clock.update(delta);


    clock.at(1800f, new Clock.TimeListener() {
      @Override
      public void handle(Clock.TimeUpdateEvent event) {
        store.getMinigameStore().getObjective().getWinner();
      }
    });
  }

  /**
   * Method to end the minigame and send the players back to the main board
   */
  public void endMinigame(Action action, UUID playerId) {
    Player player = store.getPlayer(playerId);
    player.addPoints(100);
    store.resetMinigameStore();

    ArrayList<Serializable> args = new ArrayList<>();
    args.add(playerId);
    args.add(player.getPoints());

    aiLoop.finish();
    handler.sendToAll(new Action(Command.MINIGAME_END, args));
  }
}
