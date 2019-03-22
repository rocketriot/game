package bham.bioshock.server.handlers;

import java.util.Random;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.minigame.EndMinigameMessage;
import bham.bioshock.communication.messages.minigame.MinigameStartMessage;
import bham.bioshock.communication.messages.minigame.RequestMinigameStartMessage;
import bham.bioshock.minigame.Clock;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

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
  public void startMinigame(RequestMinigameStartMessage data, UUID playerId,
      GameBoardHandler gameBoardHandler) {
    // Create a world for the minigame
    World w = new RandomWorld();
    if (data.planetId == null) {
      logger.error("Starting minigame without a planet ID (That's OK. for tests)!");
    } else {
      planetId = data.planetId;
    }
    Objective o;
    aiLoop = new MinigameAILoop();


    Random rand = new Random();

   switch(rand.nextInt(100)%3) {
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
          // NOTE CHANGE TO CAPTURE the flag
          aiLoop.registerHandler(new KillThemAllAI(id, store, handler));
        }
        break;
    }

    aiLoop.start();
    
    setupMinigameEnd(gameBoardHandler, playerId);
    handler.sendToAll(new MinigameStartMessage(w, o));
  }

  /**
   * Starts a clock ending the minigame
   */
  private void setupMinigameEnd(GameBoardHandler gameBoardHandler, UUID playerId) {
    clock = new Clock();

    clock.at(60f, new Clock.TimeListener() {
      @Override
      public void handle(Clock.TimeUpdateEvent event) {
        if (planetId == null)
          return;
        if (minigameTimer != null) {
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
   * 
   * @param gameBoardHandler
   */
  public void endMinigame(UUID winnerId, GameBoardHandler gameBoardHandler, UUID playerId) {
    boolean capturedPlanet = false;
    int points = 100;
    if (winnerId != null) {
      if (winnerId.equals(playerId)) {
        capturedPlanet = true;
      }
    }

    EndMinigameMessage msg =
        new EndMinigameMessage(playerId, winnerId, planetId, capturedPlanet, points);
    planetId = null;

    aiLoop.finish();
    handler.sendToAll(msg);
    gameBoardHandler.endTurn(playerId);
  }

  public void updateObjective(Message message) {
    handler.sendToAll(message);
  }
}
