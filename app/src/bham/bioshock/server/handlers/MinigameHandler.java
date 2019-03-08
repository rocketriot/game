package bham.bioshock.server.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.server.ServerHandler;
import bham.bioshock.minigame.ai.KillEveryoneAI;
import bham.bioshock.minigame.ai.MinigameAI;
import bham.bioshock.minigame.objectives.KillThemAll;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.worlds.FirstWorld;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.server.ai.MinigameAILoop;

public class MinigameHandler {

  Store store;
  ServerHandler handler;
  MinigameAILoop aiLoop;
  
  public MinigameHandler (Store store, ServerHandler handler) {
    this.store = store;
    this.handler = handler;
  }
  
  /*
   * Create and seed the world, and send start game command to all clients
   */
  public void startMinigame(Action action) {
    // Create a world for the minigame
    World w = new FirstWorld();
    aiLoop = new MinigameAILoop();
    aiLoop.start();
    
    for(UUID id : store.getCpuPlayers()) {
      aiLoop.registerHandler(new KillEveryoneAI(id, store, handler));
    }

    Serializable arg = (Serializable) w;
    handler.sendToAll(new Action(Command.MINIGAME_START, arg));
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

  /**
   * Method to end the minigame and send the players back to the main board
   */
  public void endMinigame(Action action, UUID playerId){
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
