package bham.bioshock.minigame.ai;

import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.objectives.Platformer;
import bham.bioshock.server.ServerHandler;
import java.util.UUID;

public class PlatformerAi extends MinigameAI {
    public PlatformerAi(UUID id, Store store, ServerHandler handler) {
        super(id, store, handler);
    }
    
    @Override
    public void update(float delta) {
        Position goalPosition = ((Platformer) localStore.getObjective()).getGoalPosition();

    }

    private void moveTowardsMainPlayer() {
        Astronaut host = localStore.getMainPlayer();

        PlanetPosition hostPP = host.getPlanetPos();
        PlanetPosition pp = astronaut.get().getPlanetPos();

        double angleDelta = hostPP.angle - pp.angle;
        double angle = (angleDelta + 180) % 360 - 180;

        for(int i=0; i<2; i++) {
            if(angle < 0) {
                astronaut.moveLeft();
            } else {
                astronaut.moveRight();
            }
        }
    }


    private void moveTowardsGoal(float delta, Position goal) {
        /*
        we need an ai to move as if a player would, by jumping up to platforms
         */
        PlanetPosition pp = astronaut.get().getPlanetPos();

    }
}
