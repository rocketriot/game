package bham.bioshock.minigame.ai;

import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.objectives.Platformer;
import bham.bioshock.server.ServerHandler;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class PlatformerAi extends MinigameAI {
    private Platform goalPlatform;
    private ArrayList<Platform> pathToGoal;
    private int currentPlatformIndex = 0;


    public PlatformerAi(UUID id, Store store, ServerHandler handler) {
        super(id, store, handler);
    }
    
    @Override
    public void update(float delta) {
        if (goalPlatform == null) {
            goalPlatform = ((Platformer) localStore.getObjective()).getGoalPlatform();
            pathToGoal = ((Platformer) localStore.getObjective()).getPathToGoal();

            System.out.println("PATH TO GOAL: ");
            pathToGoal.forEach(platform -> System.out.print(" "+platform.toString()));
        }
        moveTowardsGoal(delta);

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


    private void moveTowardsGoal(float delta) {
        /*
        we need an ai to move as if a player would, by jumping up to platforms
         */
        PlanetPosition pp = astronaut.get().getPlanetPos();
        //System.out.println(astronaut.get().getId() + " finding platform "+currentPlatformIndex);

        //need to check which platform the astronaut is on (it may have fallen off), then set the goal to the next one in path
        //Optional<Entity> astronautOn = astronaut.get().getOnPlatform();

        Platform currentPlatform = pathToGoal.get(currentPlatformIndex);
        Optional<Entity> astronautOn = astronaut.get().getOnPlatform();
        if(astronautOn.isPresent()) {
            System.out.println(astronaut.get().toString()+" IS ON: "+astronautOn.get().toString()+ "NEXT: "+(currentPlatformIndex+1));
            if(astronautOn.get().getId().equals(currentPlatform.getId())) {
                currentPlatformIndex++;
            }
        }
        else {
            System.out.println(astronaut.get().toString()+" IS ON THE GROUND");
            currentPlatformIndex = 0;
        }
        /*if(astronaut.get().isOnPlatform(pathToGoal.get(currentPlatformIndex))) {
            System.out.println("ASTRONAUT IS ON: "+currentPlatform.toString());
            currentPlatformIndex++;
        }
        else if(astronaut.get().isOnGround()){

            currentPlatformIndex = 0;
        }*/


        if(currentPlatformIndex == pathToGoal.size()) {
            System.out.print("REACHED THE GOAL PLATFORM");
            return;
        }

        Random rand = new Random();
        if (rand.nextInt(100) < 95) {
            moveToPlatform(delta, pathToGoal.get(currentPlatformIndex));
        }
        else {
            moveToPlatform(delta, nearbyPlatform(currentPlatform));
        }

    }

    private void moveToPlatform(float delta, Platform platform) {
        PlanetPosition currentPos = astronaut.get().getPlanetPos();
        PlanetPosition platformPos = platform.getPlanetPos();



        double angleDelta = platformPos.angle - currentPos.angle;
        double angle = (angleDelta + 180) % 360 - 180;

        double toLeft = ((platform.getLeftEdge().angle - platformPos.angle) + 180) % 360 - 180;
        double toRight = ((platform.getRightEdge().angle - platformPos.angle) + 180) % 360 - 180;

        double distanceDelta = platformPos.fromCenter - currentPos.fromCenter;

        //System.out.println("angle center: "+platformPos.angle+ " | angle left "+toLeft+" | angle right:: "+toRight);

        for(int i=0; i<3; i++) {
            if(angle < toLeft) {
                astronaut.moveLeft();
            } else if(angle > toRight){
                astronaut.moveRight();
            }
        }

        astronaut.jump();

    }

    private Platform nearbyPlatform(Platform currentPlatform) {
        Position currentPosition = astronaut.get().getPos();
        ArrayList<Platform> all = localStore.getWorld().getPlatforms();
        Platform nearest = null;
        float distance = 99999999f;
        for(int i = 1; i<all.size(); i++) {
            if (!(all.get(i).getId().equals(currentPlatform.getId())) && (all.get(i).getPos().sqDistanceFrom(currentPosition) < distance)) {
                nearest = all.get(i);
            }
        }
        return nearest;
    }

}
