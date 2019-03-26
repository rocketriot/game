package bham.bioshock.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.google.inject.Singleton;

@Singleton
public class BoardGame extends Game {
  
  private Router router;
  
  /**
   * Saves the router
   * @param router
   */
  public void addRouter(Router router) {
    this.router = router;
  }
  
  /**
   * Called when the game is ready to start
   */
  @Override
  public void create() {
    Gdx.graphics.setTitle("Rocket Riot");
    router.call(Route.MAIN_MENU);
  }
}
