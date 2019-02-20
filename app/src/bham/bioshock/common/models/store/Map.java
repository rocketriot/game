package bham.bioshock.common.models.store;

import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.FirstWorld;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Map {

    /** The world */
    private World world;


    /** Platforms */
    private ArrayList<Platform> platforms = new ArrayList<>();

    /** Free Rockets */
    private ArrayList<Rectangle> freeRockets;

    /** Bound of the main planet */
    Circle mainPlanet;

    public Map(World w){
        this.world = world;
        mainPlanet = new Circle(0,0,(float)world.getPlanetRadius()-10);

        platforms.add(new Platform(world,-2300,0));
        platforms.add(new Platform(world,-2300,200));

    }


    public ArrayList<Platform> getPlatforms(){
        return platforms;
    }

}
