package bham.bioshock.minigame;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Player;

public class World {

	private Player player;
	
	public World() {
		player = new Player();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Position getPlayerPos() {
		return player.getPos();
	}
}
