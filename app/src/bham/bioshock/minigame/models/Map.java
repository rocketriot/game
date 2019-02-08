package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.World;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Map {

    /** The world */
    private World world;

    /** the occupied positions */
    private ArrayList<Position> entities;

    public Map(World world){
        this.world = world;
    }
}
