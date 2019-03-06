package bham.bioshock.minigame.objectives;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.worlds.World;

import java.util.HashMap;
import java.util.Map;

public class Platformer extends Objective {

    private Position[] positions;
    private HashMap<Astronaut, Boolean> frozen;
    private HashMap<Astronaut, Float> frozenFor;
    private HashMap<Astronaut, Float> speedBoost;
    private float maxFreeze = 3f;
    private float maxBoost = 3f;
    private Astronaut winner;

    public Platformer(World world) {
        super(world);
        this.positions = this.getWorld().getPlayerPositions();
    }

    @Override
    public Astronaut getWinner() {
        return winner;
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

    public void reachedEnd(Astronaut winner) {
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
