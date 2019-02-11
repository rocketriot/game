package bham.bioshock.communication;

public enum Command {
  // @formatter:off

  // Connecting to a server
  COMM_DISCOVER,
  COMM_DISCOVER_RESPONSE,
  TEST,

  // Creating a game
  ADD_PLAYER,
  START_GAME,

  // Game board
  GET_GAME_BOARD,
  UPDATE_GAME_BOARD,
  MOVE_PLAYER_ON_BOARD;

  // @formatter:on

  public byte[] getBytes() {
    return this.toString().getBytes();
  }
}
