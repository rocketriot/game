package bham.bioshock.communication;

public enum Command {
  // @formatter:off

  // Connecting to a server
  COMM_DISCOVER,
  COMM_DISCOVER_RESPONSE,
  TEST,

  // Creating a game
  SERVER_FULL,
  ADD_PLAYER,
  REMOVE_PLAYER,
  START_GAME,

  // Game board
  GET_GAME_BOARD,
  UPDATE_GAME_BOARD,
  MOVE_PLAYER_ON_BOARD,
  
  // Minigame
  MINIGAME_START,
  MINIGAME_DIRECT_START, // FOR TESTS ONLY
  MINIGAME_PLAYER_MOVE,
  MINIGAME_END,
  MINIGAME_BULLET,

  //JoinScreen
  JOIN_SCREEN_MOVE;

  // @formatter:on

  public byte[] getBytes() {
    return this.toString().getBytes();
  }
}
