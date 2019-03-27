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
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    /*
    These distances were determined through trial and error by logging the distance of human player as it
    approaches platforms.
     */
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


    public PlatformerAI(UUID id, Store store, MultipleConnectionsHandler handler) {
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
            /*
             * Determine if the player is frozen.
             * The platformer objective contains a hash map of players mapped to frozen status
             * If a player is frozen, it should 'skip' a turn
             */
            Platformer o = (Platformer) store.getMinigameStore().getObjective();
            if(o.checkIfFrozen(astronaut.get().getId())) {
                long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                long frozen = o.getFrozenFor(astronaut.get().getId());
                /*
                Compare the current time to the time at which the player was frozen
                 */
                if((now - frozen) > (o.MAX_FROZEN_TIME)) {
                    /*
                    Enough time has elapsed so the player should now be unfrozen
                     */
                    o.setFrozen(astronaut.get().getId(),false, now);
                }
                else {
                    /*
                    player is frozen, return without updating the stack
                     */
                    return;
                }
            }


            /*
             * Initiate a new stack
             */
            if(statesStack.isEmpty()) {
                statesStack.add(new FindNextPlatform());
            }
            /*
            Execute the state at the stop of the stack
             */
            statesStack.get(0).broadcast();
            statesStack.get(0).execute();
            statesStack.get(0).updateStack();
        }

    }

    /**
     * State to determine the next goal platform
     */
    private class FindNextPlatform extends CPUState {
        FindNextPlatform() {
            super("findNextPlatform");
        }

        @Override
        void execute() {
            logger.trace("ASTRO "+astronaut.get().toString() + " current index: "+currentPlatformIndex);
            /*
            An element of randomness:
             */
            Random rand = new Random();
            int r = rand.nextInt(100);
            /*
            Most of the time, the player will move to the next platform in the pre-configured path.
             */
            if (r < 95) {
              try {
                nextPlatform = pathToGoal.get(currentPlatformIndex);
              } catch(IndexOutOfBoundsException e) {
                return;
              }
            }
            /*
            Sometimes the player will move to a nearby platform
             */
            else if(r < 99){
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
            /*
            Sometimes, the player will move to a completely random platform
             */
            else {
                int i = rand.nextInt(localStore.getWorld().getPlatforms().size());
                nextPlatform = localStore.getWorld().getPlatforms().get(i);
            }
        }

        @Override
        void updateStack() {
            /*
            Replace the current state with a new state to move the player to its next goal platform.
             */
            statesStack.remove(this);
            statesStack.add(new MoveTowardsPlatform());

        }


    };

    /**
     * State to move the player towards its next goal platform
     */
    private class MoveTowardsPlatform extends CPUState {
        /*
        The direction that the player is currently travelling in
        1 = left, 2 = right
         */
        private int direction;

        MoveTowardsPlatform() {
            super("moveTowardsPlatformm");
        }

        @Override
        void execute() {
            direction = 0;

            /*
            Determine the angle between the player's current position and the position of the platform
             */
            PlanetPosition currentPos = astronaut.get().getPlanetPos();
            PlanetPosition platformPos = nextPlatform.getPlanetPos();


            double angleDelta = platformPos.angle - currentPos.angle;
            /*
            Normalise the angle
             */
            double angle = (angleDelta + 180) % 360 - 180;

            double toLeft = ((nextPlatform.getLeftEdge().angle - platformPos.angle) + 180) % 360 - 180;
            double toRight = ((nextPlatform.getRightEdge().angle - platformPos.angle) + 180) % 360 - 180;

            double distanceDelta = platformPos.fromCenter - currentPos.fromCenter;

            /*
            Move in the direction of the planet
             */
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
            /*
            Add a state to make the astronaut jump
            Do not remove the current state because the astronaut will need to continue
            moving towards the platform after it jumps.
             */
            statesStack.add(0, new DetermineJumpingPosition(direction));

        }


    };

    /**
     * State to determine when a how to jump
     */
    private class DetermineJumpingPosition extends CPUState{
        /*
        The direction the player is currently travelling
         */
        private int direction;

        DetermineJumpingPosition(int direction) {
            super("determineJumpingPosition");
            this.direction = direction;
        }

        @Override
        void execute() {
            /*
            Get how far the player is from the platform
             */
            double distance = astronaut.get().getPos().sqDistanceFrom(nextPlatform.getPos());


            if ( distance > JUMP_MAX) {
                //The astronaut is too far from the platform to jump, do nothing
            }
            else if ( distance > JUMP_MID){
                //jump without moving, this will be a long jump
                astronaut.jump();

            }
            else if(distance > JUMP_MIN){
                //move a little in the opposite direction to slow the astronaut down
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
            /*
            Determine if the jump has been successful
             */
            Optional<Entity> astronautOn = astronaut.get().getOnPlatform();

            /*
            The astronaut is on a platform
             */
            if(astronautOn.isPresent()) {
                logger.trace(astronaut.get().toString()+" IS ON: "+astronautOn.get().toString()+ "NEXT: "+(currentPlatformIndex+1));
                /*
                Is it the correct platform?
                 */
                if(astronautOn.get().getId().equals(nextPlatform.getId())) {
                    /*
                    Increment the currentPlatformIndex to move to the next platform in the path
                     */
                    currentPlatformIndex++;
                    statesStack.clear();
                    statesStack.add(new FindNextPlatform());
                }
                else {
                    /*
                    The stack will revert back to moveTowardsPlatform to continue moving to the
                    target platform
                     */
                    logger.trace("Astronaut is on a platform but not the one it's meant to be on");
                    statesStack.remove(this);
                }
            }
            else {
                /*
                If astronaut is not on a platform, it must be on the ground.
                 */
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
