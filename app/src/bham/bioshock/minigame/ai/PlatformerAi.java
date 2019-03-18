package bham.bioshock.minigame.ai;

import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.objectives.Platformer;
import bham.bioshock.server.ServerHandler;

import java.util.ArrayList;
import java.util.UUID;

public class PlatformerAi extends MinigameAI {
    private Platform goalPlatform;
    private Platform currentPlatform;

    public PlatformerAi(UUID id, Store store, ServerHandler handler) {
        super(id, store, handler);
    }
    
    @Override
    public void update(float delta) {
        if (goalPlatform == null) {
            goalPlatform = ((Platformer) localStore.getObjective()).getGoalPlatform();
        }
        moveTowardsGoal(delta, goalPlatform);

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


    private void moveTowardsGoal(float delta, Platform goal) {
        /*
        we need an ai to move as if a player would, by jumping up to platforms
         */
        PlanetPosition pp = astronaut.get().getPlanetPos();

        ArrayList<Platform> path = ((Platformer) localStore.getObjective()).getPathToGoal();
        //TEST
        if(path.size() > 0) {
            moveToPlatform(delta, path.get(0));
        }



    }

    private void moveToPlatform(float delta, Platform platform) {
        PlanetPosition currentPos = astronaut.get().getPlanetPos();
        PlanetPosition platformPos = platform.getPlanetPos();

        double angleDelta = platformPos.angle - currentPos.angle;
        double angle = (angleDelta + 180) % 360 - 180;

        for(int i=0; i<2; i++) {
            if(angle < 0) {
                astronaut.moveLeft();
            } else {
                astronaut.moveRight();
            }
        }

        /*if((currentPos.fromCenter - platformPos.fromCenter )<0.1) {
            System.out.println("ON THE PLATFORM");
        }
        /*
        if(astronaut.get().isOnPlatform(platform))
         */
        astronaut.get().isOnPlatform(platform);
        astronaut.jump();
    }

}
