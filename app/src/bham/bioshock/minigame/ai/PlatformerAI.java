package bham.bioshock.minigame.ai;

import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.objectives.Platformer;
import bham.bioshock.minigame.physics.CollisionHandler;
import bham.bioshock.minigame.physics.StepsGenerator;
import bham.bioshock.server.ServerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;


/**
 * PlatformerAI uses a stack based Finite State Machine implementation
 * Each state extends an abstract class and implements methods execute() and updateStack().
 * execute() contains the functions of the state
 * updateStack() determines when/if and how the next state is added to the stack.
 */
public class PlatformerAI extends MinigameAI {

    private static final Logger logger = LogManager.getLogger(PlatformerAI.class);


    /**
     * initialise a new stack
     */
    private ArrayList<CPUState> statesStack = new ArrayList<>();

    /**
     * The platform which contains the goal and the path from the ground to it.
     */
    private Platform goalPlatform;
    private ArrayList<Platform> pathToGoal;

    private double JUMP_MIN = 50000;
    private double JUMP_MID = 80000;
    private double JUMP_MAX = 1600000;

    /**
     * currentPlatformIndex indicates which platform (in the path) the cpu must reach next
     * 0 indicates that the player is on the ground.
     */
    private int currentPlatformIndex = 0;
    private Platform nextPlatform;


    private StepsGenerator testStepsGenerator;


    public PlatformerAI(UUID id, Store store, ServerHandler handler) {
        super(id, store, handler);

    }

    /**
     * Called everytime the game updates
     * If the goalPlatform is null, this indicates the first update of the minigame,
     * the path to the goal is got from the world
     *
     * Execute the next state by popping the stop element of the states stack
     * @param delta the time between iterations
     */
    @Override
    public void update(float delta) {
        if (goalPlatform == null) {
            goalPlatform = ((Platformer) localStore.getObjective()).getGoalPlatform();
            pathToGoal = ((Platformer) localStore.getObjective()).getPathToGoal();
        }
        if (testStepsGenerator == null) {
            if(astronaut.get().loaded() ){
                testStepsGenerator = new StepsGenerator(store.getMinigameStore().getWorld(), astronaut.get());
                testStepsGenerator.setCollisionHandler(new CollisionHandler(store.getMinigameStore()));
                testStepsGenerator.generate();
            }

        }
        else {

            if(statesStack.isEmpty()) {
                statesStack.add(new FindNextPlatform());
            }
            statesStack.get(0).broadcast();
            statesStack.get(0).execute();
            statesStack.get(0).updateStack();
        }

    }

    private class FindNextPlatform extends CPUState {
        FindNextPlatform() {
            super("findNextPlatform");
        }

        @Override
        void execute() {
            logger.trace("ASTRO "+astronaut.get().toString() + " current index: "+currentPlatformIndex);
            Random rand = new Random();
            if (rand.nextInt(100) < 95) {
                nextPlatform = pathToGoal.get(currentPlatformIndex);
            }
            else {
                Position currentPosition = astronaut.get().getPos();
                ArrayList<Platform> all = localStore.getWorld().getPlatforms();
                Platform nearest = null;
                float distance = 99999999f;
                for(int i = 1; i<all.size(); i++) {
                    if (!(all.get(i).getId().equals(pathToGoal.get(currentPlatformIndex).getId())) && (all.get(i).getPos().sqDistanceFrom(currentPosition) < distance)) {
                        nearest = all.get(i);
                    }
                }
                nextPlatform = nearest;
            }
        }

        @Override
        void updateStack() {
            statesStack.remove(this);
            statesStack.add(new MoveTowardsPlatform());

        }


    };

    private class MoveTowardsPlatform extends CPUState {
        private int direction;

        MoveTowardsPlatform() {
            super("moveTowardsPlatformm");
        }

        @Override
        void execute() {
            direction = 0;
            PlanetPosition currentPos = astronaut.get().getPlanetPos();
            PlanetPosition platformPos = nextPlatform.getPlanetPos();


            double angleDelta = platformPos.angle - currentPos.angle;
            double angle = (angleDelta + 180) % 360 - 180;

            double toLeft = ((nextPlatform.getLeftEdge().angle - platformPos.angle) + 180) % 360 - 180;
            double toRight = ((nextPlatform.getRightEdge().angle - platformPos.angle) + 180) % 360 - 180;

            double distanceDelta = platformPos.fromCenter - currentPos.fromCenter;

            //System.out.println("angle center: "+platformPos.angle+ " | angle left "+toLeft+" | angle right:: "+toRight);


                if(angle < 0) {
                    astronaut.moveLeft();
                    direction = 1;
                } else {
                    astronaut.moveRight();
                    direction = 2;
                }
        }

        @Override
        void updateStack() {
            statesStack.add(0, new DetermineJumpingPosition(direction));


        }


    };

    private class DetermineJumpingPosition extends CPUState{
        private int direction;

        DetermineJumpingPosition(int direction) {
            super("determineJumpingPosition");
        }

        @Override
        void execute() {
            double distance = astronaut.get().getPos().sqDistanceFrom(nextPlatform.getPos());

            if ( distance > JUMP_MAX) {
                //do nothing
            }
            else if ( distance > JUMP_MID){
                //jump without moving
                astronaut.jump();

            }
            else if(distance > JUMP_MIN){
                //move a little in the opposite direction
                astronaut.jump();
                if(direction == 1) {
                    astronaut.moveRight();
                }
                else {
                    astronaut.moveLeft();
                }
            }
            else {
                //move a bit more in the opposite direction
                astronaut.jump();
                if(direction == 1) {
                    astronaut.moveRight();
                    astronaut.moveRight();
                }
                else {
                    astronaut.moveLeft();
                    astronaut.moveLeft();
                }
            }


        }

        @Override
        void updateStack() {
            statesStack.remove(this);
            statesStack.add(0, new JumpOntoPlatform(direction));
        }


    };


    private class JumpOntoPlatform extends CPUState {
        private int direction;
        JumpOntoPlatform(int direction) {

            super("jumpOntoPlatform");
            this.direction = direction;
        }

        @Override
        void execute() {
            astronaut.jump();
        }

        @Override
        void updateStack() {
            Optional<Entity> astronautOn = astronaut.get().getOnPlatform();

            if(astronautOn.isPresent()) {
                logger.trace(astronaut.get().toString()+" IS ON: "+astronautOn.get().toString()+ "NEXT: "+(currentPlatformIndex+1));
                if(astronautOn.get().getId().equals(nextPlatform.getId())) {
                    currentPlatformIndex++;
                    statesStack.remove(this);
                    statesStack.add(new FindNextPlatform());
                }
                else {
                    logger.trace("Astronaut is on a platform but not the one it's meant to be on");
                }
            }
            else {
                logger.trace(astronaut.get().toString()+" IS ON THE GROUND");
                currentPlatformIndex = 0;
                statesStack.clear();
                statesStack.add(new FindNextPlatform());
            }
        }

    };



    abstract public class CPUState {

        private final String stateName;

        CPUState(String name) {
            this.stateName = name;
        }

        abstract void execute();
        abstract void updateStack();

        public void broadcast() {
            logger.trace("State: "+ stateName);
        }

    }
}
