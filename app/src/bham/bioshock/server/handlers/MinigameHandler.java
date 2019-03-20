package bham.bioshock.server.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.minigame.Clock;
import bham.bioshock.minigame.ai.KillEveryoneAI;
import bham.bioshock.minigame.ai.PlatformerAi;
import bham.bioshock.minigame.objectives.CaptureTheFlag;
import bham.bioshock.minigame.objectives.KillThemAll;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.objectives.Platformer;
import bham.bioshock.minigame.worlds.RandomWorld;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.server.ServerHandler;
import bham.bioshock.server.ai.MinigameAILoop;

public class MinigameHandler {
  
  private static final Logger logger = LogManager.getLogger(MinigameHandler.class);
  
  Store store;
  ServerHandler handler;
  MinigameAILoop aiLoop;
  Thread minigameTimer;
  Clock clock;
  UUID planetId;

  public MinigameHandler(Store store, ServerHandler handler) {
    this.store = store;
    this.handler = handler;
  }

  /*
   * Create and seed the world, and send start game command to all clients
   */
  public void startMinigame(Action action, UUID playerId, GameBoardHandler gameBoardHandler) {
    // Create a world for the minigame
    World w = new RandomWorld();
    if(action.getArguments().size() != 0) {
      planetId = (UUID) action.getArgument(0);      
    } else {
      logger.error("Starting minigame without a planet ID (That's OK. for tests)!");
    }
    Objective o;
    aiLoop = new MinigameAILoop();

    Random rand = new Random();

    switch(rand.nextInt(100)%4) {
      case 1:
        o = new CaptureTheFlag(w);
        for (UUID id : store.getCpuPlayers()) {
          //NOTE CHANGE TO CAPTURE the flag
          aiLoop.registerHandler(new KillEveryoneAI(id, store, handler));
        }
        break;
      case 2:
        o = new Platformer(w);
        for (UUID id : store.getCpuPlayers()) {
          aiLoop.registerHandler(new PlatformerAi(id, store, handler));
        }
        break;
      case 3:
        o = new KillThemAll();
        for (UUID id : store.getCpuPlayers()) {
          aiLoop.registerHandler(new KillEveryoneAI(id, store, handler));
        }
        break;
      default:
        o = new CaptureTheFlag(w);
        for (UUID id : store.getCpuPlayers()) {
          //NOTE CHANGE TO CAPTURE the flag
          aiLoop.registerHandler(new KillEveryoneAI(id, store, handler));
        }
        break;
    }
    o = new Platformer(w);
    for (UUID id : store.getCpuPlayers()) {
      aiLoop.registerHandler(new PlatformerAi(id, store, handler));
    }
    //testing
    
    aiLoop.start();
   if(planetId != null) {
      setupMinigameEnd(gameBoardHandler, playerId);
   }

    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add((Serializable) w);
    arguments.add((Serializable) o);
    handler.sendToAll(new Action(Command.MINIGAME_START, arguments));
  }
  
  /**
   * Starts a clock ending the minigame
   */
  private void setupMinigameEnd(GameBoardHandler gameBoardHandler, UUID playerId) {
    clock = new Clock();
    long t = System.currentTimeMillis();

    clock.at(60f, new Clock.TimeListener() {
      @Override
      public void handle(Clock.TimeUpdateEvent event) {
        if(minigameTimer != null) {
          minigameTimer.interrupt();
        }
        MinigameStore localStore = store.getMinigameStore();
        Objective o = localStore.getObjective();
        endMinigame(o.getWinner(), gameBoardHandler, playerId);          
      }
    });
    
    minigameTimer = new Thread() {
      private long time;

      public void run() {
        time = System.currentTimeMillis();
        try {
          while (!isInterrupted()) {
            long delta = (System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
            clock.update((int) delta);
            Thread.sleep(1000);
          }
        } catch (InterruptedException e) {
          // Clock interrupted
        }
      }
    };
    minigameTimer.start();
  }

  /**
   * Sync player movement and position
   */
  public void playerMove(Action action, UUID playerId) {
    handler.sendToAllExcept(action, playerId);
  }

  /**
   * Create new bullet
   */
  public void bulletShot(Action action, UUID playerId) {
    handler.sendToAllExcept(action, playerId);
  }

  /**
   * Method to end the minigame and send the players back to the main board
   * @param gameBoardHandler 
   */
  public void endMinigame(UUID winnerId, GameBoardHandler gameBoardHandler, UUID playerId) {
    ArrayList<Serializable> args = new ArrayList<>();
    args.add(winnerId);
    args.add(planetId);
    args.add(100);
    planetId = null;

    aiLoop.finish();
    handler.sendToAll(new Action(Command.MINIGAME_END, args));
    gameBoardHandler.endTurn(playerId);
  }
}
