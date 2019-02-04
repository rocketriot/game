package bham.bioshock.minigame;

import java.util.ArrayList;

import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;

public class World {

	private Player mainPlayer;
	private ArrayList<Player> players;
	private ArrayList<Rocket> rockets;
	
	public World() {
		mainPlayer = new Player();
		players = new ArrayList<>();
		rockets = new ArrayList<>();
		
		seed();
	}
	
	public void seed() {
		// Seed players
		players.add(new Player(-140, 0));
		players.add(new Player(140, 0));
		players.add(new Player(220, 0));	
		
		// Seed rockets
		rockets.add(new Rocket(320, 0, 1));
		rockets.add(new Rocket(-720, 0, 2));
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
