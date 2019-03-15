package bham.bioshock.minigame.objectives;

import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.EntityType;
import bham.bioshock.minigame.models.Goal;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.World;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Platformer extends Objective {

    private Position[] positions;
    private HashMap<Astronaut, Boolean> frozen;
    private HashMap<Astronaut, Float> frozenFor;
    private HashMap<Astronaut, Float> speedBoost;
    private float maxFreeze = 3f;
    private float maxBoost = 3f;
    private Astronaut winner;
    private Position goalPosition;
    private Goal goal;

    public Platformer(World world) {
        super(world);
        this.positions = this.getWorld().getPlayerPositions();
        generateGoalPosition(world);
    }

    @Override
    public Astronaut getWinner() {
        //if the winner is set, return the winner, if not, return the closest player
        if(winner != null) { return winner; }
        else {
            Position goalPos = goal.getPos();
            float best = 99999999f;
            Astronaut bestP = null;
            Iterator it = getPlayers().iterator();
            while(it.hasNext()) {
                Astronaut player = (Astronaut) it.next();
                Position playerPos = player.getPos();
                float diff = playerPos.sqDistanceFrom(goalPos);
                if(diff < best) {
                    best = diff;
                    bestP = player;
                }
            }
            return bestP;
        }
    }

    @Override
    public void gotShot(Astronaut player, Astronaut shooter) {
        /* when the player is shot, they should freeze for a certain amount of time */
        if(!checkIfFrozen(player)) {
            setFrozen(player, true);
        }
    }

    @Override
    public void initialise() {
        getPlayers().forEach(player -> {
            frozen.put(player, false);
            frozenFor.put(player, 0f);
            speedBoost.put(player, 1f);
        });
    }

    @Override
    public void seed(MinigameStore store) {
        store.addEntity(goal);
    }

    @Override
    public void captured(Astronaut a) {
        return;
    }

    @Override
    public String instructions() {
        return null;
    }

    public Position getGoalPosition() {
        return goalPosition;
    }

    private void generateGoalPosition(World world) {
        /* generate a feasable position for the end goal */
        ArrayList<Platform> platforms = this.getWorld().getPlatforms();
        Position highestPlatform = platforms.get(0).getPos();
        for(int i = 0; i < platforms.size(); i++) {
            Platform platform = this.getWorld().getPlatforms().get(i);
            if(platform.getY() > highestPlatform.y) {
                highestPlatform = platform.getPos();
            }
        }
        /* set the goal to the highest platform */
        goalPosition = highestPlatform;
        goal = new Goal(world, highestPlatform.x, highestPlatform.y, true, EntityType.GOAL);

    }



    public boolean checkIfFrozen(Astronaut player) {
        return frozen.get(player);
    }
    public void setFrozen(Astronaut player, boolean status) {
        frozen.put(player, status);
        frozenFor.put(player, 0f);
    }

    public void countDown(float delta) {
        for (Map.Entry<Astronaut, Float> astronautFloatEntry : frozenFor.entrySet()) {
            float newValue = astronautFloatEntry.getValue().floatValue() + delta;
            if (newValue >= maxFreeze) {
                setFrozen(astronautFloatEntry.getKey(), false);
            }
        }
    }

    public void reachedGoal(Astronaut winner) {
        this.winner = winner;
    }

    public void boostSpeed(Astronaut player, float boost) {
        if(!(speedBoost.get(player) >= boost)) {
            speedBoost.put(player, boost);
        }
    }

    public float getSpeedBoost(Astronaut player) {
        return speedBoost.get(player);
    }


}
