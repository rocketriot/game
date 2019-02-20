package bham.bioshock;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.badlogic.gdx.Files.FileType;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.modules.BoardGameModule;

public class Main {

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new BoardGameModule());

    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.foregroundFPS = 60;
    config.allowSoftwareMode = true;
    config.addIcon("app/assets/ui/favicon_128.png", FileType.Internal);
    config.addIcon("app/assets/ui/favicon_32.png", FileType.Internal);
    
    BoardGame game = injector.getInstance(BoardGame.class);
    Router router = injector.getInstance(Router.class);
    router.setInjector(injector);
    
    game.addRouter(router);
    
    new LwjglApplication(game, config);
  }
}
