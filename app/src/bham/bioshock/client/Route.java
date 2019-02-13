package bham.bioshock.client;

import bham.bioshock.client.controllers.Controller;
import bham.bioshock.client.controllers.*;

public enum Route {
//@formatter:off
  
  // ROUTES
  
  MAIN_MENU         (MainMenuController.class,      "show"),
  HOST_GAME         (MainMenuController.class,      "hostGame"),
  
  HOW_TO            (HowToController.class,         "show"),
  LOADING           (LoadingController.class,       "show"),
  PREFERENCES       (PreferencesController.class,   "show"),
  
  JOIN_SCREEN       (JoinScreenController.class,    "show"),
  CLIENT_CONNECT    (JoinScreenController.class,    "connect"),
  ADD_PLAYER        (JoinScreenController.class,    "addPlayer"),
  
  GAME_BOARD        (GameBoardController.class,     "show"),
  START_GAME        (GameBoardController.class,     "start"),
  GAME_BOARD_SAVE   (GameBoardController.class,     "saveGameBoard");
  
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
