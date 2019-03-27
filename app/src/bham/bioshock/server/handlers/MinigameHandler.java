package bham.bioshock.server.handlers;

import bham.bioshock.Config;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Planet;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.utils.Clock;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.minigame.EndMinigameMessage;
import bham.bioshock.communication.messages.minigame.MinigameStartMessage;
import bham.bioshock.communication.messages.minigame.SpawnEntityMessage;
import bham.bioshock.communication.messages.objectives.EndPlatformerMessage;
import bham.bioshock.communication.messages.objectives.KillAndRespawnMessage;
import bham.bioshock.minigame.ai.CaptureTheFlagAI;
import bham.bioshock.minigame.ai.KillThemAllAI;
import bham.bioshock.minigame.ai.PlatformerAI;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Heart;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
   * @param playerId
   * @param planetId
   * @param gbHandler
   * @param objectiveId
   */
  public void startMinigame(UUID playerId, UUID planetId, GameBoardHandler gbHandler, Integer objectiveId) {
    // Create a world for the minigame
    World w = new RandomWorld();
    
    GameBoard board = store.getGameBoard();
    if(board != null) {
      Planet planet = board.getPlanet(planetId);
      if(planet != null) {
        w.setPlanetRadius(planet.getMinigameRadius());
        w.setPlanetTexture(planet.getMinigameTextureId());
      }
    }
    w.init();
    
    if (planetId == null) {
      logger.error("Starting minigame without a planet ID (That's OK. for tests)!");
    } else {
      this.planetId = planetId;
    }
    Objective o;
    aiLoop = new MinigameAILoop();
    aiLoop.reset();
    
    if(objectiveId == null) {
      Random rand = new Random();
      objectiveId = rand.nextInt(10) % 3;
    }
     // objectiveId
    switch (1) {
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
    
    setupMinigameEnd(gbHandler);
    handler.sendToAll(new MinigameStartMessage(w, o, planetId));
  }
  
  /**
   * Starts a clock ending the minigame
   */
  private void setupMinigameEnd(GameBoardHandler gameBoardHandler) {

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
        endMinigame(winner, gameBoardHandler);
      }
    });
    
    // Spawn entities every 3 seconds
    clock.every(8f, new Clock.TimeListener() {
      @Override
      public void handle(Clock.TimeUpdateEvent event) {
        spawnEntities();
      }
    });
    
    if(minigameTimer != null && minigameTimer.isAlive()) {
      minigameTimer.interrupt();
    }
    
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
  
  private void spawnEntities() {
    MinigameStore localStore = store.getMinigameStore();
    if(!(localStore.getObjective() instanceof Platformer)) {
      Heart heart = Heart.getRandom(localStore.getWorld());
      handler.sendToAll(new SpawnEntityMessage(heart));      
    }
  }
  
  private void spawnGun() {
    MinigameStore localStore = store.getMinigameStore();
    if(!(localStore.getObjective() instanceof Platformer)) {
      Position pos = localStore.getWorld().getRandomPosition();
      Gun gun = new Gun(localStore.getWorld(), pos.x, pos.y);
      handler.sendToAll(new SpawnEntityMessage(gun));      
    }
  }

  /**
   * Sync player movement and position
   */
  public void playerMove(Message message, UUID playerId) {
    MinigameStore localStore = store.getMinigameStore();
    if(localStore == null) return;
    //for platform, check if the player is frozen
    if(localStore.getObjective() instanceof Platformer) {
      Platformer o = (Platformer) store.getMinigameStore().getObjective();
      if(o.checkIfFrozen(playerId)) {
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long frozen = o.getFrozenFor(playerId);
        if((now - frozen) > o.MAX_FROZEN_TIME) {
//          o.setFrozen(playerId,false, now);
          handler.sendToAllExcept(message, playerId);
        }
        else {
          return;
        }
      }
    }
    else {
      handler.sendToAllExcept(message, playerId);
    }

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
  public void endMinigame(UUID winnerId, GameBoardHandler gameBoardHandler) {
    if(minigameTimer != null) {
      minigameTimer.interrupt();
      clock.reset();
    }
    
    UUID initiatorId = store.getMovingPlayer().getId();
    Message msg = new EndMinigameMessage(initiatorId, winnerId, planetId, Config.PLANET_POINTS);
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
  public void updateObjective(Message message, GameBoardHandler gbHandler) {
    if(message instanceof EndPlatformerMessage) {
      EndPlatformerMessage data = (EndPlatformerMessage) message;
      endMinigame(data.winnerID, gbHandler);
      return;
    } else if(message instanceof KillAndRespawnMessage) {
      KillAndRespawnMessage data = (KillAndRespawnMessage) message;
      Astronaut player = store.getMinigameStore().getPlayer(data.playerId);
      if(player != null && player.getEquipment().haveGun) {
        this.spawnGun();
      }
    }
    
    handler.sendToAll(message);
  }

}
