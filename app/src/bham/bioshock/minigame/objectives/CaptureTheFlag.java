package bham.bioshock.minigame.objectives;

import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Flag;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.worlds.World;

import java.util.HashMap;
import java.util.Random;

public class CaptureTheFlag extends Objective {

    private Position respawnPosition;
    private HashMap<Player, Float> health = new HashMap<>();
    private float initialHealth = 50.0f;
    private Position[] positions;
    private Position flagPosition;
    private Flag flag;


    public CaptureTheFlag(World world) {
        super(world);
        this.positions = this.getWorld().getPlayerPositions();
        setRandonRespawnPosition();
        setFagPosition();
        this.flag = new Flag(world,flagPosition.x , flagPosition.y, true);

    }

    @Override
    public Player getWinner() {
        return null;
    }

    @Override
    public void gotShot(Player player, Player killer) {
        if(checkIfdead(player)) {
            player.setPosition(respawnPosition);
            setPlayerHealth(initialHealth, player);
        } else {
            float newHealth = health.get(player) - 10.0f;
            setPlayerHealth(newHealth, player);
        }

    }

    @Override
    public void initialise() {

    }

    @Override
    public void seed(MinigameStore store) {
        store.addOther(flag);

    }

    private void setRandonRespawnPosition(){
        Random r = new Random();
        int i = r.nextInt()%4;
        respawnPosition = positions[i];
    }
    private boolean checkIfdead(Player p){
        if(health.get(p) - 5.0f <=0)
            return true;
        return false;
    }
    private void setPlayerHealth(float newHealth, Player p){
        health.computeIfPresent(p, (k, v) -> newHealth);
    }

    // to be changed
    private void setFagPosition(){
        flagPosition = new Position(-2320.0f, 0.0f);
    }
}
