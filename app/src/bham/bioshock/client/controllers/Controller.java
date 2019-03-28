package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Screen;

/** Root controller used by all other controllers */
public abstract class Controller {

  protected Store store;
  protected Router router;
  protected BoardGame game;

  /**
   * Store main store, router and game object
   *
   * @param store
   * @param router
   * @param game
   */
  public Controller(Store store, Router router, BoardGame game) {
    this.store = store;
    this.router = router;
    this.game = game;
  }

  /**
   * Change the screen and save it in the store
   *
   * @param screen
   */
  public void setScreen(Screen screen) {
    store.setScreen(screen);
    game.setScreen(screen);
  }
}
