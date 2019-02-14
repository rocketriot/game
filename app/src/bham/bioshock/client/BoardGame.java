package bham.bioshock.client;

import com.badlogic.gdx.Game;
import com.google.inject.Singleton;

@Singleton
public class BoardGame extends Game {
  
  private Router router;
  
  @Override
  public void create() {
    router.call(Route.MAIN_MENU);
  }
  
  public void addRouter(Router router) {
    this.router = router;
  }
}
