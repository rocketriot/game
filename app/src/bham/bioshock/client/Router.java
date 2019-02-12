package bham.bioshock.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.badlogic.gdx.Screen;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import bham.bioshock.client.controllers.Controller;
import bham.bioshock.common.models.Store;

@Singleton
public class Router {

  private static final Logger logger = LogManager.getLogger(Router.class);

  private Store store;
  private BoardGame game;
  private Injector injector;

  @Inject
  public Router(Store store, BoardGame game) {
    this.store = store;
    this.game = game;
  }

  public void setInjector(Injector injector) {
    this.injector = injector;
  }

  public void back() {
    call(Route.MAIN_MENU);
  }

  public void call(Route view) {
    Class<? extends Controller> c = view.getController();
    String method = view.getMethod();
    Controller controller = injector.getInstance(c);

    try {
      
      for(Method m : c.getDeclaredMethods())
      {
        String name = m.getName();
        if(name.startsWith(method)) {
          logger.debug("Executing " + c.toString() + " : " + method);
          m.invoke(controller, new Object[0]);
          return;
        }
      }
      logger.error("No method " + method + " in " + c.getName());
      
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      logger.error("Method " + method + " in " + c.getName() + " connot be called! ");
      e.printStackTrace();
    }
  }

  public void changeScreen(Screen screen) {
    game.setScreen(screen);
  }

}
