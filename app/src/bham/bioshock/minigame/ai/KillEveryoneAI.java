package bham.bioshock.minigame.ai;

import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.server.ServerHandler;

public class KillEveryoneAI extends MinigameAI {

  private static final Logger logger = LogManager.getLogger(KillEveryoneAI.class);

  public KillEveryoneAI(UUID id, Store store, ServerHandler handler) {
    super(id, store, handler);
  }

  @Override
  public void update(float delta) {
    astronaut.jump(delta);

  }

}
