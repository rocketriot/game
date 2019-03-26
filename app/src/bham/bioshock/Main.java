package bham.bioshock;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.badlogic.gdx.Files.FileType;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.modules.GameModule;

public class Main {
  
  private static final Logger logger = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    // Create app injector. It will use dependency injection to create our classes
    Injector injector = Guice.createInjector(new GameModule());

    // Setup Lwjgl configuration
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.foregroundFPS = 60;
    config.allowSoftwareMode = true;
    config.addIcon("app/assets/ui/favicon_128.png", FileType.Internal);
    config.addIcon("app/assets/ui/favicon_32.png", FileType.Internal);
    
    // Create instances of game and router
    BoardGame game = injector.getInstance(BoardGame.class);
    Router router = injector.getInstance(Router.class);
    
    // Save the injector in the router
    router.setInjector(injector);
    
    try {
      // Prebuild the routing
      router.preBuild();
    } catch(Exception e) {
      logger.fatal(e.getMessage());
      logger.fatal("Game cannot be run! Error while creating the routing!");
      return;
    }
    
    game.addRouter(router);
    
    new LwjglApplication(game, config);
  }
}
