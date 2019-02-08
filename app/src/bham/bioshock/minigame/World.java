package bham.bioshock.minigame;

import java.util.ArrayList;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;

public class World {

	private Player mainPlayer;
	private ArrayList<Player> players;
	private ArrayList<Rocket> rockets;
	
	public static final Position GRAVITY_POS = new Position(0f, 0f);
	public static final double PLANET_RADIUS = 2000;
	public static final double GRAVITY = 200;
	
	
	public World() {
		mainPlayer = new Player(10, 2100);
		
		players = new ArrayList<>();
		rockets = new ArrayList<>();
		
		seed();
	}
	
	public void seed() {
		// Seed players
		players.add(new Player(-2140, 0));
		players.add(new Player(2140, 2100));
		players.add(new Player(220, -2100));
		
		// Seed rockets
		rockets.add(new Rocket(-300, 2000, 1));
		rockets.add(new Rocket(300, 2000, 2));
		Rocket flying = new Rocket(-2500, 2550, 3);
		flying.setSpeed(90, 1200.0f);
		flying.setRotation(270);
		rockets.add(flying);
	}
	
	public Player getMainPlayer() {
		return mainPlayer;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public ArrayList<Rocket> getRockets() {
		return rockets;
	}
}
