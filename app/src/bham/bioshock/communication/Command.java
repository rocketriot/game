package bham.bioshock.communication;

public enum Command {
	COMM_DISCOVER, COMM_DISCOVER_RESPONSE, TEST, ADD_PLAYER, START_GAME, GET_GAME_BOARD, UPDATE_GAME_BOARD;

	public byte[] getBytes() {
		return this.toString().getBytes();
	}
}
