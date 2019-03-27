package bham.bioshock.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import bham.bioshock.client.controllers.Controller;

@Singleton
public class Router {

  private static final Logger logger = LogManager.getLogger(Router.class);

  /**
   * Guice injector for creating new controller instances
   */
  private Injector injector;
  
  /**
   * Maps the route to the instance of the method and controller.
   */
  private HashMap<Route, Method> routing = new HashMap<>();

  /**
   * Setting the injector
   * @param injector
   */
  public void setInjector(Injector injector) {
    this.injector = injector;
  }
  
  /**
   * Displays the main menu screen
   */
  public void back() {
    call(Route.MAIN_MENU);
  }

  /** 
   * Call controller method with arguments
   * 
   * @param route
   * @param arg for controller method
   */
  public void call(Route route, Object arg) {
    try {
      // Get the method from the routing cache
      Method method = routing.get(route);
      // Create controller instance and inject dependencies
      Controller controller = injector.getInstance(route.getController());
      
      if(arg != null) {
        // There're some arguments, so put them in the Object[] array, and invoke the method
        Object[] args = new Object[1];
        args[0] = arg;
        logger.trace("Executing " + route.getController().getName() + " : " + route.getMethod() + " : " + arg.toString());
        method.invoke(controller, args);
      } else {
        // No arguments, just run the method
        logger.trace("Executing " + route.getController().getName() + " : " + route.getMethod());
        method.invoke(controller);
      }
      
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      logger.error("Method " + route.getMethod() + " in " + route.getController().getName() + " connot be called! ");
      e.printStackTrace();
    }
  }
  
  /**
   * Call the route without arguments
   * @param view
   */
  public void call(Route view) {
    call(view, null);
  }

  /**
   * Prebuild the routing
   * This method will find appropriate method in the controller, display any issues in the console
   * and cache methods in the HashMap so they can be found easily later
   */
  public void preBuild() throws Exception {
    for (Route route : Route.values()) {
      Class<? extends Controller> c = route.getController();
      String method = route.getMethod();
      
      boolean saved = false;
        // Iterate over all controller's method and find the one
      for(Method m : c.getDeclaredMethods())
      {
        // If the name match, put the method in the cache
        if(m.getName().equals(method)) {
          routing.put(route, m);
          saved = true;
          break;
        }
      }
      // Display error if the method cannot be cached
      if(!saved) {
        throw new Exception("No method " + method + " in " + c.getName());     
      }
    }
  }
 
}
