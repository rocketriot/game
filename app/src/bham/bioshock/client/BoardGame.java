package bham.bioshock.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.google.inject.Singleton;

@Singleton
public class BoardGame extends Game {
  
  private Router router;
  
  @Override
  public void create() {
    Gdx.graphics.setTitle("Rocket Riot");

    router.call(Route.MAIN_MENU);
  }
  
  public void addRouter(Router router) {
    this.router = router;
  }
}
