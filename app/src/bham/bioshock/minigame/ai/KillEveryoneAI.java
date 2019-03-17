package bham.bioshock.minigame.ai;

import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.server.ServerHandler;

public class KillEveryoneAI extends MinigameAI {

  private static final Logger logger = LogManager.getLogger(KillEveryoneAI.class);

  public KillEveryoneAI(UUID id, Store store, ServerHandler handler) {
    super(id, store, handler);
  }

  @Override
  public void update(float delta) {
    Astronaut host = localStore.getMainPlayer();
    
    PlanetPosition hostPP = host.getPlanetPos();
    PlanetPosition pp = astronaut.get().getPlanetPos();
    
    double angleDelta = hostPP.angle - pp.angle;
    double angle = (angleDelta + 180) % 360 - 180;
    
    if(angle < 0) {
      astronaut.moveLeft();
    } else {
      astronaut.moveRight();
    }
  }

}
