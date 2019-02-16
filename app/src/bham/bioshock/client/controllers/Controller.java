package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import com.badlogic.gdx.Screen;
import bham.bioshock.client.screens.ScreenMaster;
import bham.bioshock.common.models.store.Store;

/** Root controller used by all other controllers */
public abstract class Controller {

  protected Store store;
  protected Router router;
  protected BoardGame game;

  public Controller(Store store, Router router, BoardGame game) {
    this.store = store;
    this.router = router;
    this.game = game;
  }
  
  public void setScreen(Screen screen) {
    store.setScreen(screen);
    game.setScreen(screen);
  }

}
