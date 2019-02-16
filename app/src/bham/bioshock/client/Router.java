package bham.bioshock.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import bham.bioshock.client.controllers.Controller;

@Singleton
public class Router {

  private static final Logger logger = LogManager.getLogger(Router.class);

  private Injector injector;
 
  public void setInjector(Injector injector) {
    this.injector = injector;
  }

  public void back() {
    call(Route.MAIN_MENU);
  }

  /** 
   * Call controller method
   * 
   * @param route
   * @param arg for controller method
   */
  public void call(Route route, Object arg) {
    Class<? extends Controller> c = route.getController();
    String method = route.getMethod();
    Controller controller = injector.getInstance(c);

    try {
      
      for(Method m : c.getDeclaredMethods())
      {
        String name = m.getName();
        if(name.startsWith(method)) {
          if(arg != null) {
            Object[] args = new Object[1];
            args[0] = arg;
            logger.debug("Executing " + c.toString() + " : " + method + " : " + arg.toString());
            m.invoke(controller, args);
          } else {
            logger.debug("Executing " + c.toString() + " : " + method);
            m.invoke(controller, new Object[0]);
          }
          return;
        }
      }
      logger.error("No method " + method + " in " + c.getName());
      
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      logger.error("Method " + method + " in " + c.getName() + " connot be called! ");
      e.printStackTrace();
    }
  }
  
  public void call(Route view) {
    call(view, null);
  }

}
