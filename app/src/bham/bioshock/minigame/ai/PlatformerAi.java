package bham.bioshock.minigame.ai;

import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.server.ServerHandler;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.objectives.Platformer;

import java.util.UUID;

public class PlatformerAi extends MinigameAI {
    private Position goalPosition;

    public PlatformerAi(UUID id, Store store, ServerHandler handler) {
        super(id, store, handler);

        goalPosition = ((Platformer) localStore.getObjective()).getGoalPosition();
    }

    @Override
    public void update(float delta) {

    }

    private void moveTowardsMainPlayer(float delta) {
        Astronaut host = localStore.getMainPlayer();

        PlanetPosition hostPP = host.getPlanetPos();
        PlanetPosition pp = astronaut.getPlanetPos();

        double angleDelta = hostPP.angle - pp.angle;
        double angle = (angleDelta + 180) % 360 - 180;

        for(int i=0; i<2; i++) {
            if(angle < 0) {
                astronaut.moveLeft(delta);
            } else {
                astronaut.moveRight(delta);
            }
        }
    }


    private void moveTowardsGoal(float delta, Position goal) {
        /*
        we need an ai to move as if a player would, by jumping up to platforms
         */
        PlanetPosition pp = astronaut.getPlanetPos();

    }
}
