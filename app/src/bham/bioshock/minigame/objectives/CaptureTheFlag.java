package bham.bioshock.minigame.objectives;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Flag;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.Random;

public class CaptureTheFlag extends Objective {

    private Position respawnPosition;
    private HashMap<Astronaut, Float> health = new HashMap<>();
    private float initialHealth = 50.0f;
    private Position[] positions;
    private Position flagPosition;
    private Flag flag;
    private Astronaut flagOwner = null;


    public CaptureTheFlag(World world) {
        super(world);
        this.positions = this.getWorld().getPlayerPositions();
        setRandonRespawnPosition();
        Position p= new Position(-2350.0f, 100.0f);
        setFlagPosition(p);


        this.flag = new Flag(world,flagPosition.x , flagPosition.y, true);

    }

    @Override
    public Astronaut getWinner() {
        return flagOwner;
    }

    // router call minigame move
    
    @Override
    public void gotShot(Astronaut player, Astronaut killer) {
        if(checkIfdead(player)) {
            setFlagPosition(player.getPos());
            flag.setIsRemoved(false);
            player.setPosition(respawnPosition);
            getRouter().call(Route.MINIGAME_MOVE);
            setPlayerHealth(initialHealth, player);

        } else {
            float newHealth = health.get(player) - 10.0f;
            setPlayerHealth(newHealth, player);
        }

    }

    @Override
    public void initialise() {
        getPlayers().forEach(player -> {
            health.put(player, initialHealth);
        });

    }

    @Override
    public void seed(MinigameStore store) {
        store.addOther(flag);

    }

    @Override
    public void captured(Astronaut a) {
        setFlagOwner(a);

    }

    private void setRandonRespawnPosition(){
        Random r = new Random();
        int i = Math.abs(r.nextInt()%4);
        respawnPosition = positions[i];
    }
    private boolean checkIfdead(Astronaut p){
        if(health.get(p) - 5.0f <=0)
            return true;
        return false;
    }
    private void setPlayerHealth(float newHealth, Astronaut p){
        health.computeIfPresent(p, (k, v) -> newHealth);
    }

    private void setFlagPosition(Position p){
        flagPosition = p;
    }

    public void setFlagOwner(Astronaut owner) {
        this.flagOwner = owner;
    }
}
