package bham.bioshock.common.models.store;
import bham.bioshock.minigame.models.Platform;
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
        this.world = w;
        mainPlanet = new Circle(0,0,(float)world.getPlanetRadius()-10);

        platforms.add(new Platform(world,-2050, 200));
        platforms.add(new Platform(world,-2050, 600));
        platforms.add(new Platform(world,-2100, 300));
        platforms.add(new Platform(world,-2250, 900));
        platforms.add(new Platform(world,-2450, 1100));
        platforms.add(new Platform(world,-2550, 1300));
        platforms.add(new Platform(world,-2550, 1400));
        platforms.add(new Platform(world,-2050, 1600));
        platforms.add(new Platform(world,-2050, 1400));
    }


    public ArrayList<Platform> getPlatforms(){
        return platforms;
    }

}