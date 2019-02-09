package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.World;
import com.badlogic.gdx.graphics.g3d.particles.values.RectangleSpawnShapeValue;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Map {

    /** The world */
    private World world;

    /** Platforms */
    private ArrayList<Rectangle> platforms;

    public Map(World world){
        this.world = world;
        Circle mainPlanet = new Circle(0,0,(float)world.PLANET_RADIUS-10);
    }

    public void addPlatform(Rectangle platform){
        platforms.add(platform);
    }

    public ArrayList<Rectangle> getPlatforms(){
        return platforms;
    }

}
