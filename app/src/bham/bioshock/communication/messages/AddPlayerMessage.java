package bham.bioshock.communication.messages;

import java.util.UUID;

import bham.bioshock.common.models.Player;

public class AddPlayerMessage extends Message {

	private static final long serialVersionUID = 5273994102561800829L;
	
	public final UUID id;
	public final String username;
	public final Boolean isCPU;
	
	public AddPlayerMessage(UUID id, String username, Boolean isCPU) {
		this.id = id;
		this.username = username;
		this.isCPU = isCPU;
	}
	
	public AddPlayerMessage(Player player) {
		this.id = player.getId();
		this.username = player.getUsername();
		this.isCPU = player.isCpu();
	}
	
	public Player getPlayer() {
		return new Player(id, username, isCPU);
	}

}