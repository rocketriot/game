package bham.bioshock.server.handlers;

import bham.bioshock.minigame.ai.CaptureTheFlagAI;
import java.util.Random;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.Config;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.utils.Clock;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.minigame.EndMinigameMessage;
import bham.bioshock.communication.messages.minigame.MinigameStartMessage;
import bham.bioshock.communication.messages.minigame.RequestMinigameStartMessage;
import bham.bioshock.minigame.ai.KillThemAllAI;
import bham.bioshock.minigame.ai.PlatformerAI;
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

  public MinigameHandler(Store store, ServerHandler handler, Clock clock) {
    this.store = store;
    this.handler = handler;
    this.clock = clock;
  }

  /**
   * Creates and sends minigame start request to all clients
   * Creates and seed the world
   * Starts minigame timer to ending the game
   * 
   * 
   * @param data
   * @param playerId
   * @param gameBoardHandler
   */
  public void startMinigame(UUID playerId, UUID planetId, GameBoardHandler gbHandler, Integer objectiveId) {
    // Create a world for the minigame
    World w = new RandomWorld();
    if (planetId == null) {
      logger.error("Starting minigame without a planet ID (That's OK. for tests)!");
    } else {
      this.planetId = planetId;
    }
    Objective o;
    aiLoop = new MinigameAILoop();
    
    if(objectiveId == null) {
      Random rand = new Random();
      objectiveId = rand.nextInt(10) % 3;
    }

    switch (objectiveId) {
      case 1:
        o = new Platformer(w);
        for (UUID id : store.getCpuPlayers()) {
          aiLoop.registerHandler(new PlatformerAI(id, store, handler));
        }
        break;
      case 2:
        o = new KillThemAll();
        for (UUID id : store.getCpuPlayers()) {
          aiLoop.registerHandler(new KillThemAllAI(id, store, handler));
        }
        break;
      default:
        o = new CaptureTheFlag(w);
        for (UUID id : store.getCpuPlayers()) {
          aiLoop.registerHandler(new CaptureTheFlagAI(id, store, handler));
        }
        break;
    }

    aiLoop.start();

    setupMinigameEnd(gbHandler, playerId);
    handler.sendToAll(new MinigameStartMessage(w, o));
  }


  /**
   * Starts a clock ending the minigame
   */
  private void setupMinigameEnd(GameBoardHandler gameBoardHandler, UUID playerId) {

    clock.reset();
    clock.at(60f, new Clock.TimeListener() {
      @Override
      public void handle(Clock.TimeUpdateEvent event) {
        logger.info("Ending minigame");
        if (planetId == null)
          return;
        if (minigameTimer != null) {
          minigameTimer.interrupt();
        }
        MinigameStore localStore = store.getMinigameStore();
        UUID winner = null;
        if(localStore != null && localStore.getObjective() != null) {
          winner = localStore.getObjective().getWinner();
        }
        endMinigame(winner, gameBoardHandler, playerId);
      }
    });

    minigameTimer = new Thread("MinigameTimer") {
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
  public void playerMove(Message message, UUID playerId) {
    handler.sendToAllExcept(message, playerId);
  }

  public void playerStep(Message message, UUID playerId) {
    handler.sendToAllExcept(message, playerId);
  }

  /**
   * Create new bullet
   */
  public void bulletShot(Message message, UUID playerId) {
    handler.sendToAllExcept(message, playerId);
  }

  /**
   * Method to end the minigame and send the players back to the main board
   * called by the inner timer
   * 
   * @param gameBoardHandler
   */
  private void endMinigame(UUID winnerId, GameBoardHandler gameBoardHandler, UUID playerId) {
    Message msg = new EndMinigameMessage(playerId, winnerId, planetId, Config.PLANET_POINTS);
    planetId = null;

    aiLoop.finish();
    handler.sendToAll(msg);
    gameBoardHandler.endTurn();
  }

  /**
   * Send objective update to all clients
   * 
   * @param message
   */
  public void updateObjective(Message message) {
    handler.sendToAll(message);
  }

}
