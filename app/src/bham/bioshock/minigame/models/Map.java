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
        entities = new ArrayList<Position>();
        setOccupiedPlaces();
    }

    public void setOccupiedPlaces(){
        Player main = world.getMainPlayer();
        entities.add(main.getPos());
        entities.addAll(main.getBorder());

        ArrayList<Player> players = world.getPlayers();
        ArrayList<Rocket> rockets = world.getRockets();

        players.forEach(e-> entities.addAll(e.getBorder()));
        rockets.forEach(e-> entities.addAll(e.getBorder()));
    }
}
