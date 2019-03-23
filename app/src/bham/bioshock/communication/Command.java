package bham.bioshock.communication;

public enum Command {
  // @formatter:off

  // Connecting to a server
  COMM_DISCOVER_REQ,
  COMM_DISCOVER_RES,

  // Creating a game
  SERVER_FULL,
  REGISTER,
  ADD_PLAYER,
  REMOVE_PLAYER,
  START_GAME,
  RECONNECT_PLAYER,

  // Game board
  GET_GAME_BOARD,
  UPDATE_GAME_BOARD,
  UPDATE_TURN,
  MOVE_PLAYER_ON_BOARD,
  END_TURN,
  
  // Minigame
  MINIGAME_START,
  MINIGAME_DIRECT_START, // FOR TESTS ONLY
  MINIGAME_PLAYER_MOVE,
  MINIGAME_PLAYER_STEP,
  MINIGAME_END,
  MINIGAME_BULLET,
  MINIGAME_UPDATE_OBJECTIVE,
  MINIGAME_OBJECTIVE,

  //JoinScreen
  JOIN_SCREEN_MOVE;

  // @formatter:on

  public byte[] getBytes() {
    return this.toString().getBytes();
  }
}
