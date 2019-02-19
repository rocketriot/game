package bham.bioshock.minigame.models;

import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.worlds.FirstWorld;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Map {

    /** The world */
    private MinigameStore world;


    /** Platforms */
    private ArrayList<Rectangle> platforms;

    /** Free Rockets */
    private ArrayList<Rectangle> freeRockets;

    public Map(MinigameStore world){
        this.world = world;
        Circle mainPlanet = new Circle(0,0,(float)world.getPlanetRadius()-10);
        freeRockets =  new ArrayList<Rectangle>();
    }
    public void addRocket(Rectangle r) {
        if (!freeRockets.contains(r)) {
            freeRockets.add(r);
        }
    }
    public ArrayList<Rectangle> getRockets(){
        return freeRockets;
    }

    public void addPlatform(Rectangle platform){
        platforms.add(platform);
    }

    public ArrayList<Rectangle> getPlatforms(){
        return platforms;
    }

}
