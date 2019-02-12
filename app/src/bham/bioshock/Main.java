package bham.bioshock;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.modules.BoardGameModule;

public class Main {

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new BoardGameModule());

    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.foregroundFPS = 60;
    
    BoardGame game = injector.getInstance(BoardGame.class);
    Router router = injector.getInstance(Router.class);
    router.setInjector(injector);
    
    game.addRouter(router);
    
    new LwjglApplication(game, config);
  }
}
