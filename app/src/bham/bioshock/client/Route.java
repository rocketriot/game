package bham.bioshock.client;

import bham.bioshock.client.controllers.Controller;
import bham.bioshock.client.controllers.*;
import com.badlogic.gdx.audio.Sound;

public enum Route {
//@formatter:off
  
  // ROUTES
  
  MAIN_MENU             (MainMenuController.class,      "show"),
  HOST_GAME             (MainMenuController.class,      "hostGame"),
  ALERT                 (MainMenuController.class,      "alert"),
  
  HOW_TO                (HowToController.class,         "show"),
  LOADING               (LoadingController.class,       "show"),
  PREFERENCES           (PreferencesController.class,   "show"),

  JOIN_SCREEN       (JoinScreenController.class,    "show"),
  ADD_PLAYER        (JoinScreenController.class,    "addPlayer"),
  REMOVE_PLAYER     (JoinScreenController.class,    "removePlayer"),
  DISCONNECT_PLAYER (JoinScreenController.class,    "disconnectPlayer"),
  START_GAME        (JoinScreenController.class,    "start"),
  JOIN_SCREEN_MOVE (JoinScreenController.class, "rocketMove"),
  JOIN_SCREEN_UPDATE (JoinScreenController.class, "updateRocket"),


  MOVE_PLAYER           (GameBoardController.class,     "move"),
  MOVE_RECEIVED         (GameBoardController.class,     "moveReceived"),
  GAME_BOARD_SAVE       (GameBoardController.class,     "saveGameBoard"),
  PLAYERS_SAVE          (GameBoardController.class,     "savePlayers"),
  GAME_BOARD_SHOW       (GameBoardController.class,     "show"),
  SKIP_TURN             (GameBoardController.class,     "skipTurn"),

  DIRECT_MINIGAME_START (MinigameController.class,      "directStart"), // FOR TESTS ONLY
  SEND_MINIGAME_START   (MinigameController.class,      "sendStart"),
  START_MINIGAME        (MinigameController.class,      "show"),
  MINIGAME_MOVE         (MinigameController.class,      "playerMove"),
  MINIGAME_PLAYER_UPDATE(MinigameController.class,      "updatePlayer"),
  SERVER_MINIGAME_END   (MinigameController.class,      "sendEnd"),
  MINIGAME_END          (MinigameController.class,      "end"),
  MINIGAME_BULLET       (MinigameController.class,      "bulletCreate"),
  MINIGAME_BULLET_SEND  (MinigameController.class,      "bulletShot"),

  START_MUSIC           (SoundController.class,         "startMusic"),
  FADE_OUT              (SoundController.class,         "fadeOut"),
  MUSIC_VOLUME          (SoundController.class,         "setMusicVolume"),
  MUSIC_ENABLED         (SoundController.class,         "enableMusic"),
  LOOP_SOUND            (SoundController.class,         "loopSound"),
  STOP_SOUND            (SoundController.class,         "stopSound"),
  SOUNDS_VOLUME         (SoundController.class,         "setSoundsVolume"),
  SOUNDS_ENABLED        (SoundController.class,         "enableSounds");
  
//@formatter:on

  private Class<? extends Controller> controllerClass;
  private String method;

  Route(Class<? extends Controller> controllerClass, String method) {
    this.controllerClass = controllerClass;
    this.method = method;
  }

  public Class<? extends Controller> getController() {
    return controllerClass;
  }

  public String getMethod() {
    return method;
  }

}
